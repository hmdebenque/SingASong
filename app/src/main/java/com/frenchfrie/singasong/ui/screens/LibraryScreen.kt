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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.mutableStateOf
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
    var dialog by rememberSaveable { mutableStateOf("") }
    var targetArrangement by rememberSaveable { mutableStateOf("") }
    var importLabel by rememberSaveable { mutableStateOf("") }
    var importPart by rememberSaveable { mutableStateOf("") }
    val scorePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { practice.importUri(it, ImportKind.SCORE, targetArrangement, importLabel, importPart) }
    }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { practice.importUri(it, ImportKind.AUDIO, targetArrangement, importLabel, importPart) }
    }
    val enabled = !state.loading && !state.importing
    val song = state.library.song
    BackHandler(song != null && dialog.isEmpty()) { if (enabled) practice.backToLibrary() }
    Scaffold(modifier = modifier.imePadding(), contentWindowInsets = WindowInsets.safeDrawing) { contentPadding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(contentPadding)) {
            val compactHeight = (maxHeight < 600.dp && maxWidth < 720.dp) || LocalDensity.current.fontScale > 1.3f
            Column(
                Modifier.fillMaxSize().then(if (compactHeight && song != null) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp).semantics { heading() })
                state.error?.let { error ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(error), color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f).testTag("error"), style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = practice::dismissError) { Text(stringResource(R.string.dismiss)) }
                    }
                    if (error == R.string.restore_error) TextButton(onClick = practice::retryRestore, enabled = enabled) { Text(stringResource(R.string.retry_restore)) }
                }
                if (state.library.conversionFailed) {
                    Text(stringResource(R.string.conversion_error), color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = practice::retryRestore, enabled = enabled) { Text(stringResource(R.string.retry_restore)) }
                }
                if (state.loading || state.importing) Text(stringResource(if (state.loading) R.string.loading else R.string.importing), modifier = Modifier.testTag("busy"))
                if (song == null) {
                    var query by rememberSaveable { mutableStateOf("") }
                    val results = state.library.search(query)
                    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item(key = "heading") { Text(stringResource(R.string.library_title), style = MaterialTheme.typography.headlineSmall) }
                        item(key = "actions") {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { dialog = "song-new" }, enabled = enabled, modifier = Modifier.testTag("new-song")) { Text(stringResource(R.string.new_song)) }
                                TextButton(onClick = practice::loadDemo, enabled = enabled, modifier = Modifier.testTag("load-demo")) { Text(stringResource(R.string.load_demo)) }
                            }
                        }
                        item(key = "search") {
                            OutlinedTextField(query, { query = it }, label = { Text(stringResource(R.string.search_library)) },
                                singleLine = true, modifier = Modifier.fillMaxWidth().testTag("search"))
                        }
                        if (state.library.songs.isEmpty()) item(key = "empty") {
                            Text(stringResource(R.string.library_empty_title), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.library_empty_body))
                        } else if (results.isEmpty()) item(key = "empty") { Text(stringResource(R.string.no_results)) }
                        items(results, key = { it.id }) { item ->
                            Card(onClick = { if (enabled) practice.openSong(item.id) }, modifier = Modifier.fillMaxWidth().testTag("song-${item.id}")) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                                    if (item.composer.isNotBlank()) Text(item.composer)
                                    if (item.tags.isNotBlank()) Text(item.tags, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = practice::backToLibrary, enabled = enabled) { Text(stringResource(R.string.library_back)) }
                        TextButton(onClick = { dialog = "song-edit" }, enabled = enabled) { Text(stringResource(R.string.edit_song)) }
                        Button(onClick = { dialog = "materials" }, enabled = enabled, modifier = Modifier.testTag("materials")) { Text(stringResource(R.string.materials)) }
                    }
                    Text(song.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
                    Text(listOfNotNull(state.library.arrangement?.name, state.library.arrangement?.arranger?.takeIf { it.isNotBlank() },
                        state.library.arrangement?.voicing?.takeIf { it.isNotBlank() }).joinToString(" · "), style = MaterialTheme.typography.labelLarge)
                    if (state.offlineReady) Text(stringResource(R.string.offline_ready), style = MaterialTheme.typography.labelMedium)
                    BoxWithConstraints((if (compactHeight) Modifier.height(640.dp) else Modifier.weight(1f)).fillMaxWidth()) {
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
    if (dialog == "materials" && song != null) EditorDialog(stringResource(R.string.materials), { dialog = "" }) {
        Text(song.title, style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.arrangements))
        state.library.arrangements.filter { it.songId == song.id }.forEach { arrangement ->
            TextButton(onClick = { practice.selectArrangement(arrangement.id) }, enabled = enabled) {
                Text((if (arrangement.id == state.library.arrangement?.id) "✓ " else "") + arrangement.name)
            }
        }
        FlowRow {
            TextButton(onClick = { dialog = "arrangement-new" }) { Text(stringResource(R.string.new_arrangement)) }
            TextButton(onClick = { dialog = "arrangement-edit" }) { Text(stringResource(R.string.edit_arrangement)) }
        }
        for (kind in ImportKind.entries) {
            Text(stringResource(if (kind == ImportKind.SCORE) R.string.scores else R.string.recordings), style = MaterialTheme.typography.titleMedium)
            val resources = state.library.resources.filter { it.arrangementId == state.library.arrangement?.id && it.kind == kind }
            if (resources.isEmpty()) Text(stringResource(R.string.no_resources))
            resources.forEach { resource ->
                val selected = resource.id == state.workspace.scoreId || resource.id == state.workspace.audioId
                TextButton(onClick = { practice.selectResource(resource.id) }, enabled = enabled, modifier = Modifier.testTag("resource-${resource.id}")) {
                    Text((if (selected) "✓ " else "") + resource.displayLabel)
                }
            }
            TextButton(onClick = { dialog = if (kind == ImportKind.SCORE) "import-score" else "import-audio" }, enabled = enabled) {
                Text(stringResource(if (kind == ImportKind.SCORE) R.string.import_score else R.string.import_audio))
            }
        }
    }
    if (dialog.startsWith("song-") || dialog.startsWith("arrangement-") || dialog.startsWith("import-")) {
        val mode = dialog
        val isSong = mode.startsWith("song-")
        val isImport = mode.startsWith("import-")
        val arrangement = state.library.arrangement
        var name by rememberSaveable(mode) { mutableStateOf(if (mode == "song-edit") song?.title.orEmpty() else if (mode == "arrangement-edit") arrangement?.name.orEmpty() else "") }
        var detail by rememberSaveable(mode) { mutableStateOf(if (mode == "song-edit") song?.composer.orEmpty() else if (mode == "arrangement-edit") arrangement?.arranger.orEmpty() else "") }
        var extra by rememberSaveable(mode) { mutableStateOf(if (mode == "song-edit") song?.tags.orEmpty() else if (mode == "arrangement-edit") arrangement?.voicing.orEmpty() else "") }
        var invalid by rememberSaveable(mode) { mutableStateOf(false) }
        val title = stringResource(when (mode) {
            "song-new" -> R.string.new_song; "song-edit" -> R.string.edit_song
            "arrangement-new" -> R.string.new_arrangement; "arrangement-edit" -> R.string.edit_arrangement
            "import-score" -> R.string.import_score; else -> R.string.import_audio
        })
        EditorDialog(title, { dialog = "" }) {
            OutlinedTextField(name, { name = it; invalid = false }, label = { Text(stringResource(if (isImport) R.string.resource_label else if (isSong) R.string.song_title else R.string.arrangement_name)) },
                isError = invalid, modifier = Modifier.fillMaxWidth().testTag("edit-name"))
            if (!isImport) OutlinedTextField(detail, { detail = it }, label = { Text(stringResource(if (isSong) R.string.composer else R.string.arranger)) }, modifier = Modifier.fillMaxWidth().testTag("edit-detail"))
            OutlinedTextField(extra, { extra = it }, label = { Text(stringResource(if (isSong) R.string.tags else if (isImport) R.string.voice_part else R.string.voicing)) }, modifier = Modifier.fillMaxWidth().testTag("edit-extra"))
            if (isImport) Text(stringResource(R.string.part_hint), style = MaterialTheme.typography.bodySmall)
            if (invalid) Text(stringResource(R.string.name_validation), color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("validation"))
            FlowRow {
                TextButton(onClick = { dialog = "" }) { Text(stringResource(R.string.cancel)) }
                Button(onClick = {
                    val duplicate = !isSong && !isImport && state.library.arrangements.any {
                        it.songId == song?.id && (mode != "arrangement-edit" || it.id != arrangement?.id) &&
                            com.frenchfrie.singasong.library.searchKey(it.name) == com.frenchfrie.singasong.library.searchKey(name.trim())
                    }
                    invalid = name.isBlank() || listOf(name, detail, extra).any { it.trim().length > 500 } || duplicate
                    if (!invalid) {
                        when {
                            isSong -> practice.saveSong(if (mode == "song-edit") song?.id else null, name, detail, extra)
                            isImport -> {
                                targetArrangement = arrangement!!.id; importLabel = name; importPart = extra
                                if (mode == "import-score") scorePicker.launch(arrayOf("application/pdf")) else audioPicker.launch(arrayOf("audio/*"))
                            }
                            else -> practice.saveArrangement(if (mode == "arrangement-edit") arrangement?.id else null, name, detail, extra)
                        }
                        dialog = ""
                    }
                }, enabled = enabled, modifier = Modifier.testTag("save-edit")) { Text(stringResource(if (isImport) R.string.choose_file else R.string.save)) }
            }
        }
    }
}

@Composable
private fun EditorDialog(title: String, dismiss: () -> Unit, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Dialog(onDismissRequest = dismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.padding(16.dp).widthIn(max = 560.dp).fillMaxWidth().imePadding(), shape = MaterialTheme.shapes.large) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                content()
                TextButton(onClick = dismiss) { Text(stringResource(R.string.close)) }
            }
        }
    }
}

@Composable
private fun ScorePane(state: PracticeState, practice: PracticeViewModel, modifier: Modifier = Modifier) {
    val workspace = state.workspace
    val zoom = state.zoom
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
            TextButton(onClick = practice::zoom, enabled = !state.importing) {
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
