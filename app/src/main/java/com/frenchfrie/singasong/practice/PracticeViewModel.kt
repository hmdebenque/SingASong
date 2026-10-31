package com.frenchfrie.singasong.practice

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.frenchfrie.singasong.R
import com.frenchfrie.singasong.reader.PdfReader
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PracticeState(
    val workspace: Workspace = Workspace(),
    val loading: Boolean = true,
    val importing: Boolean = false,
    val rendering: Boolean = false,
    val bitmap: Bitmap? = null,
    val playing: Boolean = false,
    val ready: Boolean = false,
    val offlineReady: Boolean = false,
    val positionMs: Long = 0,
    val error: Int? = null,
)

class PracticeViewModel(application: Application) : AndroidViewModel(application) {
    private val reader = PdfReader(application)
    private val store = WorkspaceStore(application, reader)
    private val playback = PracticePlayer(application)
    private val mutableState = MutableStateFlow(PracticeState())
    val state = mutableState.asStateFlow()
    private var renderJob: Job? = null
    private var width = 1080
    private var renderRequest = 0
    private var dirtyPosition = false

    init {
        playback.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                mutableState.value = mutableState.value.copy(playing = isPlaying)
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                mutableState.value = mutableState.value.copy(ready = playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            }
            override fun onPlayerError(error: PlaybackException) {
                mutableState.value = mutableState.value.copy(error = R.string.playback_error, ready = false, playing = false)
            }
        })
        viewModelScope.launch {
            try {
                applyWorkspace(store.load())
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                mutableState.value = mutableState.value.copy(error = R.string.restore_error)
            } finally {
                mutableState.value = mutableState.value.copy(loading = false)
            }
        }
        viewModelScope.launch {
            while (true) {
                delay(500)
                if (state.value.workspace.audioId != null && !state.value.importing) {
                    val current = playback.player.currentPosition.coerceAtLeast(0)
                    if (current != state.value.positionMs) {
                        mutableState.value = mutableState.value.copy(positionMs = current)
                        dirtyPosition = true
                    }
                    if (dirtyPosition) persist()
                }
            }
        }
    }

    private fun applyWorkspace(workspace: Workspace) {
        val old = state.value.workspace
        val stored = workspace.scoreId?.let { store.file(it).isFile } == true &&
            workspace.audioId?.let { store.file(it).isFile } == true
        val audioChanged = old.audioId != workspace.audioId
        val position = if (audioChanged) workspace.positionMs else state.value.positionMs
        mutableState.value = mutableState.value.copy(workspace = workspace, positionMs = position, offlineReady = stored)
        if (old.audioId != workspace.audioId && workspace.audioId != null) {
            val file = store.file(workspace.audioId)
            if (file.isFile) playback.open(file, workspace.positionMs)
            else mutableState.value = mutableState.value.copy(error = R.string.missing_file)
        }
        if (old.scoreId != workspace.scoreId || old.page != workspace.page) render()
    }

    fun importUri(uri: Uri, kind: ImportKind) = importWork {
        store.importUri(uri, kind) { PracticePlayer.inspect(getApplication(), it) }
    }

    fun loadDemo() = importWork {
        val app = getApplication<Application>()
        val score = store.import(ImportKind.SCORE, app.getString(R.string.demo_score), { app.assets.open("demo/score.pdf") }) {
            PracticePlayer.inspect(app, it)
        }
        applyWorkspace(score)
        store.import(ImportKind.AUDIO, app.getString(R.string.demo_recording), { app.assets.open("demo/practice.wav") }) {
            PracticePlayer.inspect(app, it)
        }
    }

    private fun importWork(block: suspend () -> Workspace) {
        if (state.value.loading || state.value.importing) return
        pause()
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(importing = true, error = null)
            try {
                persist()
                applyWorkspace(block())
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                mutableState.value = mutableState.value.copy(error = R.string.import_error)
            } finally {
                mutableState.value = mutableState.value.copy(importing = false)
            }
        }
    }

    fun renderWidth(pixels: Int) {
        val bounded = pixels.coerceIn(64, 2048)
        if (bounded != width) {
            width = bounded
            render()
        }
    }

    private fun render() {
        val request = ++renderRequest
        renderJob?.cancel()
        val snapshot = state.value.workspace
        val id = snapshot.scoreId ?: return
        mutableState.value = mutableState.value.copy(bitmap = null, rendering = true)
        renderJob = viewModelScope.launch {
            try {
                val bitmap = reader.render(store.file(id), snapshot.page, width)
                mutableState.value = mutableState.value.copy(bitmap = bitmap)
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                mutableState.value = mutableState.value.copy(error = R.string.render_error)
            } finally {
                // A cancelled old request must not clear a newer request's progress.
                if (request == renderRequest) {
                    mutableState.value = mutableState.value.copy(rendering = false)
                }
            }
        }
    }

    fun page(delta: Int) {
        if (state.value.importing) return
        val workspace = state.value.workspace
        val page = (workspace.page + delta).coerceIn(0, (workspace.pageCount - 1).coerceAtLeast(0))
        if (page == workspace.page) return
        applyWorkspace(workspace.copy(page = page))
        viewModelScope.launch { persist() }
    }

    fun togglePlayback() {
        if (state.value.ready && !state.value.importing) playback.toggle()
    }

    fun seek(positionMs: Long) {
        val position = positionMs.coerceIn(0, state.value.workspace.durationMs)
        playback.player.seekTo(position)
        mutableState.value = mutableState.value.copy(positionMs = position)
        dirtyPosition = true
        viewModelScope.launch { persist() }
    }

    fun pause() {
        playback.player.pause()
        if (state.value.workspace.audioId != null) {
            mutableState.value = mutableState.value.copy(positionMs = playback.player.currentPosition.coerceAtLeast(0))
        }
        viewModelScope.launch { persist() }
    }

    private suspend fun persist() {
        if (state.value.loading) return
        val snapshot = state.value
        try {
            store.savePosition(snapshot.workspace.scoreId, snapshot.workspace.page, snapshot.workspace.audioId, snapshot.positionMs)
            dirtyPosition = false
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            mutableState.value = mutableState.value.copy(error = R.string.save_error)
        }
    }

    fun dismissError() {
        mutableState.value = mutableState.value.copy(error = null)
    }

    override fun onCleared() {
        playback.close()
    }
}
