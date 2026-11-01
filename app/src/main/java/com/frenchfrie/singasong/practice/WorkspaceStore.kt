package com.frenchfrie.singasong.practice

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.AtomicFile
import com.frenchfrie.singasong.reader.ScoreReader
import java.io.File
import java.io.InputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class Workspace(
    val scoreId: String? = null,
    val scoreTitle: String = "",
    val pageCount: Int = 0,
    val page: Int = 0,
    val audioId: String? = null,
    val audioTitle: String = "",
    val durationMs: Long = 0,
    val positionMs: Long = 0,
)

enum class ImportKind(val maxBytes: Long, val extension: String) {
    SCORE(20L * 1024 * 1024, "pdf"),
    AUDIO(64L * 1024 * 1024, "audio"),
}

/** One private workspace; no source URI permission is required after a successful copy. */
class WorkspaceStore(private val context: Context, private val reader: ScoreReader) {
    private val directory = File(context.filesDir, "practice").apply { check(isDirectory || mkdirs()) }
    private val metadata = AtomicFile(File(directory, "workspace.json"))
    private val mutex = Mutex()
    private var cleaned = false

    fun file(id: String): File {
        require(id.matches(Regex("[a-f0-9-]{36}\\.(pdf|audio)")))
        return File(directory, id)
    }

    /** Read without deleting anything: SAS-008 conversion keeps the prototype intact. */
    suspend fun loadForConversion(): Workspace = mutex.withLock { withContext(Dispatchers.IO) { read() } }

    suspend fun load(): Workspace = mutex.withLock {
        withContext(Dispatchers.IO) {
            val current = read()
            if (!cleaned) {
                val retained = setOf(current.scoreId, current.audioId, "workspace.json", "workspace.json.bak", "workspace.json.new")
                directory.listFiles()?.filter { it.name !in retained }?.forEach { it.delete() }
                cleaned = true
            }
            current
        }
    }

    suspend fun importUri(uri: Uri, kind: ImportKind, inspectAudio: suspend (File) -> Long): Workspace {
        require(uri.scheme == "content") { "Use the system file picker" }
        val title = withContext(Dispatchers.IO) {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0)?.take(120) else null
            } ?: "Imported file"
        }
        return import(kind, title, { context.contentResolver.openInputStream(uri) ?: error("File unavailable") }, inspectAudio)
    }

    suspend fun import(
        kind: ImportKind,
        title: String,
        open: () -> InputStream,
        inspectAudio: suspend (File) -> Long,
    ): Workspace = mutex.withLock {
        val id = "${UUID.randomUUID()}.${kind.extension}"
        val staged = File(directory, "$id.incoming")
        val destination = file(id)
        try {
            withContext(Dispatchers.IO) {
                open().use { input ->
                    staged.outputStream().use { output ->
                        val buffer = ByteArray(16 * 1024)
                        var total = 0L
                        while (true) {
                            val count = input.read(buffer)
                            if (count < 0) break
                            require(count > 0) { "File provider returned no data" }
                            total += count
                            require(total <= kind.maxBytes) { "File exceeds import limit" }
                            output.write(buffer, 0, count)
                        }
                        require(total > 0) { "Empty file" }
                        output.fd.sync()
                    }
                }
            }
            val pages = if (kind == ImportKind.SCORE) reader.inspect(staged) else 0
            val duration = if (kind == ImportKind.AUDIO) inspectAudio(staged) else 0L
            require(kind != ImportKind.AUDIO || duration > 0) { "Unsupported recording" }
            withContext(Dispatchers.IO) {
                val previous = read()
                check(staged.renameTo(destination)) { "Unable to store import" }
                val next = if (kind == ImportKind.SCORE) {
                    previous.copy(scoreId = id, scoreTitle = title.take(120), pageCount = pages, page = 0)
                } else {
                    previous.copy(audioId = id, audioTitle = title.take(120), durationMs = duration, positionMs = 0)
                }
                write(next)
                // Keep the previous file until metadata is committed successfully.
                (if (kind == ImportKind.SCORE) previous.scoreId else previous.audioId)?.let { file(it).delete() }
                next
            }
        } catch (failure: Exception) {
            withContext(kotlinx.coroutines.NonCancellable + Dispatchers.IO) {
                staged.delete()
                // If the metadata commit failed, an unreferenced new file can be removed safely.
                val current = runCatching { read() }.getOrNull()
                if (current?.scoreId != id && current?.audioId != id) destination.delete()
            }
            throw failure
        }
    }

    suspend fun savePosition(scoreId: String?, page: Int, audioId: String?, positionMs: Long): Workspace = mutex.withLock {
        withContext(Dispatchers.IO) {
            val current = read()
            val updated = current.copy(
                page = if (scoreId == current.scoreId) page.coerceIn(0, (current.pageCount - 1).coerceAtLeast(0)) else current.page,
                positionMs = if (audioId == current.audioId) positionMs.coerceIn(0, current.durationMs) else current.positionMs,
            )
            write(updated)
            updated
        }
    }

    private fun read(): Workspace {
        if (!metadata.baseFile.exists() && !File(metadata.baseFile.path + ".bak").exists()) return Workspace()
        val json = metadata.openRead().bufferedReader().use { JSONObject(it.readText()) }
        require(json.getInt("version") == 1)
        fun id(key: String) = json.optString(key).takeIf { it.isNotEmpty() }?.also { file(it) }
        val pages = json.getInt("pages").coerceIn(0, 300)
        val duration = json.getLong("duration").coerceAtLeast(0)
        return Workspace(id("score"), json.optString("scoreTitle"), pages,
            json.getInt("page").coerceIn(0, (pages - 1).coerceAtLeast(0)),
            id("audio"), json.optString("audioTitle"), duration, json.getLong("position").coerceIn(0, duration))
    }

    private fun write(state: Workspace) {
        val json = JSONObject().put("version", 1).put("score", state.scoreId ?: "")
            .put("scoreTitle", state.scoreTitle).put("pages", state.pageCount).put("page", state.page)
            .put("audio", state.audioId ?: "").put("audioTitle", state.audioTitle)
            .put("duration", state.durationMs).put("position", state.positionMs)
        val stream = metadata.startWrite()
        try {
            stream.write(json.toString().toByteArray(Charsets.UTF_8))
            metadata.finishWrite(stream)
        } catch (failure: Exception) {
            metadata.failWrite(stream)
            throw failure
        }
    }
}
