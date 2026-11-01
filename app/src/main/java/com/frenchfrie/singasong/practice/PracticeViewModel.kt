package com.frenchfrie.singasong.practice

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.frenchfrie.singasong.R
import com.frenchfrie.singasong.library.Library
import com.frenchfrie.singasong.library.LibraryStore
import com.frenchfrie.singasong.reader.PdfReader
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PracticeState(
    val library: Library = Library(),
    val workspace: Workspace = Workspace(),
    val zoom: Float = 1f,
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
    private val store = LibraryStore(application, reader)
    private val playback = PracticePlayer(application)
    private val mutableState = MutableStateFlow(PracticeState())
    val state = mutableState.asStateFlow()
    private var renderJob: Job? = null
    private var width = 1080
    private var renderRequest = 0
    private var activeAudioId: String? = null

    init {
        playback.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                mutableState.value = mutableState.value.copy(playing = activeAudioId != null && isPlaying)
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                mutableState.value = mutableState.value.copy(ready = activeAudioId != null &&
                    (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED))
            }
            override fun onPlayerError(error: PlaybackException) {
                mutableState.value = mutableState.value.copy(error = R.string.playback_error, ready = false, playing = false)
            }
        })
        viewModelScope.launch {
            try { applyLibrary(store.load(::inspectAudio)) }
            catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                mutableState.value = mutableState.value.copy(error = R.string.restore_error)
            } finally { mutableState.value = mutableState.value.copy(loading = false) }
        }
        viewModelScope.launch {
            while (true) {
                delay(500)
                if (!state.value.loading && !state.value.importing && state.value.ready && activeAudioId == state.value.workspace.audioId) {
                    val position = playback.player.currentPosition.coerceAtLeast(0)
                    if (position != state.value.positionMs) {
                        mutableState.value = mutableState.value.copy(positionMs = position)
                        persist(state.value)
                    }
                }
            }
        }
    }

    private suspend fun inspectAudio(file: java.io.File) = PracticePlayer.inspect(getApplication(), file)
    private fun applyLibrary(library: Library) {
        val old = state.value.workspace
        val workspace = library.workspace()
        val selected = listOfNotNull(library.score, library.audio)
        val missing = selected.any { !store.file(it.filename).isFile }
        val audioChanged = activeAudioId != workspace.audioId
        if (audioChanged) {
            activeAudioId = null
            playback.player.stop()
            playback.player.clearMediaItems()
        }
        mutableState.value = mutableState.value.copy(library = library, workspace = workspace,
            positionMs = if (audioChanged) workspace.positionMs else state.value.positionMs,
            zoom = library.score?.zoom ?: 1f, offlineReady = selected.isNotEmpty() && !missing,
            error = if (missing) R.string.missing_file else null)
        if (audioChanged && library.audio != null) {
            val audio = library.audio!!
            if (store.file(audio.filename).isFile) {
                activeAudioId = audio.id
                playback.open(store.file(audio.filename), audio.positionMs)
            }
        }
        if (old.scoreId != workspace.scoreId || old.page != workspace.page || state.value.bitmap == null) render()
    }

    /** Pause and checkpoint the old identities before loading any new selection. */
    private fun change(error: Int = R.string.library_save_error, block: suspend () -> Library) {
        if (state.value.loading || state.value.importing) return
        pause()
        val previous = state.value
        mutableState.value = previous.copy(importing = true, error = null)
        viewModelScope.launch {
            try {
                check(persist(previous)) { "Checkpoint failed" }
                applyLibrary(block())
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                mutableState.value = mutableState.value.copy(error = error)
            } finally { mutableState.value = mutableState.value.copy(importing = false) }
        }
    }

    fun saveSong(id: String?, title: String, composer: String, tags: String) = change { store.saveSong(id, title, composer, tags) }
    fun saveArrangement(id: String?, name: String, arranger: String, voicing: String) {
        val songId = state.value.library.songId ?: return
        change { store.saveArrangement(songId, id, name, arranger, voicing) }
    }
    fun openSong(id: String) = change { store.selectSong(id) }
    fun backToLibrary() = change { store.selectSong(null) }
    fun selectArrangement(id: String) = change { store.selectArrangement(id) }
    fun selectResource(id: String) = change { store.selectResource(id) }
    fun retryRestore() = change(R.string.restore_error) { store.load(::inspectAudio) }
    fun importUri(uri: Uri, kind: ImportKind, arrangementId: String, label: String, part: String) = change(R.string.import_error) {
        store.importUri(uri, arrangementId, kind, label, part, ::inspectAudio)
    }
    fun loadDemo() = change(R.string.import_error) {
        val app = getApplication<Application>()
        var library = store.load(::inspectAudio)
        val existing = library.songs.find { it.tags == "generated-demo" }
        library = if (existing == null) store.saveSong(null, "Generated demo · Quiet Rehearsal Study", "SingASong synthetic fixture", "generated-demo")
            else store.selectSong(existing.id)
        val arrangement = library.arrangement!!.id
        if (library.resources.none { it.arrangementId == arrangement && it.kind == ImportKind.SCORE }) {
            library = store.import(arrangement, ImportKind.SCORE, app.getString(R.string.demo_score), "Full score", { app.assets.open("demo/score.pdf") }, ::inspectAudio)
        }
        if (library.resources.none { it.arrangementId == arrangement && it.kind == ImportKind.AUDIO }) {
            library = store.import(arrangement, ImportKind.AUDIO, app.getString(R.string.demo_recording), "Tutti", { app.assets.open("demo/practice.wav") }, ::inspectAudio)
        }
        library
    }

    fun renderWidth(pixels: Int) {
        val bounded = pixels.coerceIn(64, 2048)
        if (bounded != width) { width = bounded; render() }
    }
    private fun render() {
        val request = ++renderRequest
        renderJob?.cancel()
        mutableState.value = mutableState.value.copy(bitmap = null, rendering = false)
        val snapshot = state.value
        val resource = snapshot.library.resources.find { it.id == snapshot.workspace.scoreId } ?: return
        if (!store.file(resource.filename).isFile) {
            mutableState.value = mutableState.value.copy(error = R.string.missing_file)
            return
        }
        mutableState.value = mutableState.value.copy(rendering = true)
        renderJob = viewModelScope.launch {
            try {
                val bitmap = reader.render(store.file(resource.filename), snapshot.workspace.page, width)
                if (request == renderRequest) mutableState.value = mutableState.value.copy(bitmap = bitmap)
                else bitmap.recycle()
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                if (request == renderRequest) mutableState.value = mutableState.value.copy(error = R.string.render_error)
            } finally {
                if (request == renderRequest) mutableState.value = mutableState.value.copy(rendering = false)
            }
        }
    }
    fun page(delta: Int) {
        if (state.value.importing) return
        val workspace = state.value.workspace
        val page = (workspace.page + delta).coerceIn(0, (workspace.pageCount - 1).coerceAtLeast(0))
        if (page == workspace.page) return
        mutableState.value = mutableState.value.copy(workspace = workspace.copy(page = page))
        render()
        checkpoint()
    }
    fun zoom() {
        if (state.value.importing) return
        mutableState.value = mutableState.value.copy(zoom = if (state.value.zoom == 1f) 2f else 1f)
        checkpoint()
    }
    fun togglePlayback() { if (state.value.ready && !state.value.importing) playback.toggle() }
    fun seek(positionMs: Long) {
        if (!state.value.ready || state.value.importing) return
        val position = positionMs.coerceIn(0, state.value.workspace.durationMs)
        playback.player.seekTo(position)
        mutableState.value = mutableState.value.copy(positionMs = position)
        checkpoint()
    }
    fun pause() {
        playback.player.pause()
        if (activeAudioId != null && activeAudioId == state.value.workspace.audioId) {
            mutableState.value = mutableState.value.copy(positionMs = playback.player.currentPosition.coerceAtLeast(0))
        }
        checkpoint()
    }
    private fun checkpoint() {
        val snapshot = state.value
        if (!snapshot.loading) viewModelScope.launch { persist(snapshot) }
    }
    private suspend fun persist(snapshot: PracticeState): Boolean {
        return try {
            store.savePosition(snapshot.workspace.scoreId, snapshot.workspace.page, snapshot.zoom, snapshot.workspace.audioId, snapshot.positionMs)
            true
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            mutableState.value = mutableState.value.copy(error = R.string.save_error)
            false
        }
    }
    fun dismissError() { mutableState.value = mutableState.value.copy(error = null) }
    override fun onCleared() {
        playback.close()
        viewModelScope.coroutineContext[Job]?.invokeOnCompletion { store.close() }
    }
}
