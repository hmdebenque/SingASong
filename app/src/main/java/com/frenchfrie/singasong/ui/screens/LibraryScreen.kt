package com.frenchfrie.singasong.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.frenchfrie.singasong.R
import com.frenchfrie.singasong.practice.ImportKind
import com.frenchfrie.singasong.practice.PracticeState
import com.frenchfrie.singasong.practice.PracticeViewModel

@Composable
fun LibraryScreen(state: PracticeState, practice: PracticeViewModel, modifier: Modifier = Modifier) {
    val scorePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { practice.importUri(it, ImportKind.SCORE) }
    }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { practice.importUri(it, ImportKind.AUDIO) }
    }
    val enabled = !state.loading && !state.importing
    Scaffold(modifier = modifier, contentWindowInsets = WindowInsets.safeDrawing) { contentPadding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(contentPadding)) {
            val compactHeight = maxHeight < 600.dp && maxWidth < 720.dp
            Column(
                modifier = Modifier.fillMaxSize()
                    .then(if (compactHeight) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp).semantics { heading() })
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { scorePicker.launch(arrayOf("application/pdf")) }, enabled = enabled) {
                        Text(stringResource(if (state.workspace.scoreId == null) R.string.import_score else R.string.replace_score))
                    }
                    Button(onClick = { audioPicker.launch(arrayOf("audio/*")) }, enabled = enabled) {
                        Text(stringResource(if (state.workspace.audioId == null) R.string.import_audio else R.string.replace_audio))
                    }
                }
                state.error?.let { error ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(error), color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f).testTag("error"), style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = practice::dismissError) { Text(stringResource(R.string.dismiss)) }
                    }
                }
                if (state.loading || state.importing) {
                    Text(stringResource(if (state.loading) R.string.loading else R.string.importing), modifier = Modifier.testTag("busy"))
                }
                if (state.workspace.scoreId == null && state.workspace.audioId == null) {
                    Card {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.library_title), style = MaterialTheme.typography.headlineSmall)
                            Text(stringResource(R.string.library_empty_title), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.library_empty_body))
                            Button(onClick = practice::loadDemo, enabled = enabled, modifier = Modifier.testTag("load-demo")) {
                                Text(stringResource(R.string.load_demo))
                            }
                        }
                    }
                } else {
                    if (state.offlineReady) {
                        Text(stringResource(R.string.offline_ready), style = MaterialTheme.typography.labelMedium)
                    }
                    BoxWithConstraints(modifier = (if (compactHeight) Modifier.height(640.dp) else Modifier.weight(1f)).fillMaxWidth()) {
                        if (maxWidth >= 720.dp) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                                ScorePane(state, practice, Modifier.weight(1f).fillMaxHeight())
                                PlayerPane(state, practice, Modifier.width(280.dp).fillMaxHeight().verticalScroll(rememberScrollState()))
                            }
                        } else {
                            Column(Modifier.fillMaxSize()) {
                                ScorePane(state, practice, Modifier.weight(1f).fillMaxWidth())
                                PlayerPane(state, practice, Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun ScorePane(state: PracticeState, practice: PracticeViewModel, modifier: Modifier = Modifier) {
    val workspace = state.workspace
    var zoom by rememberSaveable(workspace.scoreId) { mutableFloatStateOf(1f) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (workspace.scoreId == null) {
            Text(stringResource(R.string.choose_score))
            return@Column
        }
        Text(workspace.scoreTitle, maxLines = 2, style = MaterialTheme.typography.labelLarge)
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val density = LocalDensity.current
            val viewportWidth = maxWidth
            val pixelWidth = with(density) { (maxWidth * zoom).roundToPx() }
            LaunchedEffect(pixelWidth, workspace.scoreId) { practice.renderWidth(pixelWidth) }
            if (state.rendering) CircularProgressIndicator(Modifier.align(Alignment.Center))
            state.bitmap?.let { bitmap ->
                Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).horizontalScroll(rememberScrollState())) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.score_page, workspace.page + 1, workspace.pageCount),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.width(viewportWidth * zoom).testTag("score-image"),
                    )
                }
            }
        }
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = { practice.page(-1) }, enabled = workspace.page > 0 && !state.importing) {
                Text(stringResource(R.string.previous_page))
            }
            Text(stringResource(R.string.page_count, workspace.page + 1, workspace.pageCount),
                style = MaterialTheme.typography.labelMedium, modifier = Modifier.testTag("page-count"))
            TextButton(onClick = { practice.page(1) }, enabled = workspace.page + 1 < workspace.pageCount && !state.importing) {
                Text(stringResource(R.string.next_page))
            }
            TextButton(onClick = { zoom = if (zoom == 1f) 2f else 1f }) {
                Text(stringResource(if (zoom == 1f) R.string.zoom_in else R.string.fit_width))
            }
        }
    }
}

@Composable
private fun PlayerPane(state: PracticeState, practice: PracticeViewModel, modifier: Modifier = Modifier) {
    val workspace = state.workspace
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (workspace.audioId == null) {
            Text(stringResource(R.string.choose_audio), style = MaterialTheme.typography.bodySmall)
            return@Column
        }
        Text(workspace.audioTitle, maxLines = 2, style = MaterialTheme.typography.labelLarge)
        var seek by remember(workspace.audioId) { mutableFloatStateOf(-1f) }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = practice::togglePlayback, enabled = state.ready && !state.importing, modifier = Modifier.testTag("play-pause")) {
                Text(stringResource(if (state.playing) R.string.pause else R.string.play))
            }
            Text(stringResource(R.string.playback_time, time(state.positionMs), time(workspace.durationMs)),
                style = MaterialTheme.typography.labelMedium, modifier = Modifier.testTag("playback-time"))
        }
        val description = stringResource(R.string.seek_recording)
        Slider(
            value = if (seek >= 0) seek else state.positionMs.toFloat().coerceIn(0f, workspace.durationMs.toFloat()),
            onValueChange = { seek = it },
            onValueChangeFinished = { practice.seek(seek.toLong()); seek = -1f },
            valueRange = 0f..workspace.durationMs.toFloat().coerceAtLeast(1f),
            enabled = state.ready && !state.importing,
            modifier = Modifier.testTag("seek").semantics { contentDescription = description },
        )
        Text(stringResource(R.string.foreground_playback), style = MaterialTheme.typography.bodySmall)
    }
}

private fun time(milliseconds: Long): String {
    val seconds = milliseconds.coerceAtLeast(0) / 1000
    return "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
}
