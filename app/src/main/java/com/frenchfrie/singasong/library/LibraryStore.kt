package com.frenchfrie.singasong.library

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.OpenableColumns
import com.frenchfrie.singasong.practice.ImportKind
import com.frenchfrie.singasong.practice.Workspace
import com.frenchfrie.singasong.practice.WorkspaceStore
import com.frenchfrie.singasong.reader.ScoreReader
import java.io.File
import java.io.InputStream
import java.text.Normalizer
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

fun searchKey(text: String): String = Normalizer.normalize(text, Normalizer.Form.NFD)
    .replace(Regex("\\p{M}+"), "").lowercase(Locale.ROOT)

data class Song(val id: String, val title: String, val composer: String, val tags: String, val arrangementId: String?)
data class Arrangement(val id: String, val songId: String, val name: String, val arranger: String, val voicing: String)
data class Resource(
    val id: String, val arrangementId: String, val kind: ImportKind, val label: String, val part: String,
    val filename: String, val pages: Int, val durationMs: Long,
    val page: Int = 0, val zoom: Float = 1f, val positionMs: Long = 0,
) {
    val displayLabel: String get() = if (part.isBlank()) label else "$label · $part"
}
data class Library(
    val songs: List<Song> = emptyList(), val arrangements: List<Arrangement> = emptyList(),
    val resources: List<Resource> = emptyList(), val songId: String? = null,
    val selections: Map<String, Pair<String?, String?>> = emptyMap(), val conversionFailed: Boolean = false,
) {
    val song: Song? get() = songs.find { it.id == songId }
    val arrangement: Arrangement? get() = arrangements.find { it.id == song?.arrangementId }
    val score: Resource? get() = resources.find { it.id == selections[arrangement?.id]?.first }
    val audio: Resource? get() = resources.find { it.id == selections[arrangement?.id]?.second }
    fun search(query: String): List<Song> {
        val terms = searchKey(query).split(Regex("\\s+")).filter { it.isNotBlank() }
        return songs.filter { song ->
            val text = searchKey(listOf(song.title, song.composer, song.tags,
                arrangements.filter { it.songId == song.id }.joinToString(" ") { it.arranger }).joinToString(" "))
            terms.all(text::contains)
        }.sortedWith(compareBy({ searchKey(it.title) }, { it.id }))
    }
    fun workspace() = Workspace(score?.id, score?.displayLabel.orEmpty(), score?.pages ?: 0, score?.page ?: 0,
        audio?.id, audio?.displayLabel.orEmpty(), audio?.durationMs ?: 0, audio?.positionMs ?: 0)
}

/** The only writer of library metadata/files. No destructive upgrade or orphan sweep. */
class LibraryStore(private val context: Context, private val reader: ScoreReader) : AutoCloseable {
    private val helper = Database(context)
    private val mutex = Mutex()
    private val directory = File(context.filesDir, "library").apply { check(isDirectory || mkdirs()) }
    private val db get() = helper.writableDatabase
    private var conversionFailed = false

    fun file(filename: String): File {
        require(filename.matches(Regex("[a-f0-9-]{36}\\.(pdf|audio)")))
        return File(directory, filename)
    }

    suspend fun load(inspectAudio: suspend (File) -> Long): Library = mutex.withLock {
        try {
            convertWorkspace(inspectAudio)
            conversionFailed = false
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            conversionFailed = true // Existing library remains usable; originals remain available for retry.
        }
        withContext(Dispatchers.IO) { snapshot() }
    }

    private suspend fun <T> change(block: () -> T): T = mutex.withLock {
        withContext(Dispatchers.IO) { transaction(block) }
    }
    private fun <T> transaction(block: () -> T): T {
        db.beginTransaction()
        try {
            val result = block()
            db.setTransactionSuccessful()
            return result
        } finally { db.endTransaction() }
    }
    private fun insert(table: String, vararg fields: Pair<String, Any?>) {
        db.insertOrThrow(table, null, values(*fields))
    }
    private fun values(vararg fields: Pair<String, Any?>) = ContentValues().apply {
        fields.forEach { (key, value) ->
            when (value) {
                null -> putNull(key)
                is Int -> put(key, value)
                is Long -> put(key, value)
                is Float -> put(key, value)
                else -> put(key, value.toString())
            }
        }
    }
    private fun update(table: String, id: String, vararg fields: Pair<String, Any?>) {
        check(db.update(table, values(*fields), "id = ?", arrayOf(id)) == 1)
    }
    private fun id() = UUID.randomUUID().toString()
    private fun text(value: String, required: Boolean = false): String {
        val result = value.trim()
        require(result.length <= 500 && (!required || result.isNotBlank())) { "Enter a name (up to 500 characters)" }
        return result
    }

    suspend fun saveSong(songId: String?, title: String, composer: String, tags: String): Library = change {
        val fields = arrayOf("title" to text(title, true), "composer" to text(composer), "tags" to text(tags))
        if (songId == null) {
            val song = id()
            insert("songs", "id" to song, *fields)
            val arrangement = addArrangement(song, "Default arrangement", "", "")
            update("songs", song, "arrangement_id" to arrangement)
            update("settings", "main", "song_id" to song)
        } else update("songs", songId, *fields)
        snapshot()
    }

    suspend fun saveArrangement(songId: String, arrangementId: String?, name: String, arranger: String, voicing: String): Library = change {
        val cleanName = text(name, true)
        // Names must distinguish arrangements under one song, including case/accent variants.
        val current = snapshot()
        require(current.songs.any { it.id == songId })
        require(current.arrangements.none { it.songId == songId && it.id != arrangementId && searchKey(it.name) == searchKey(cleanName) })
        val selected = if (arrangementId == null) addArrangement(songId, cleanName, text(arranger), text(voicing)) else {
            require(current.arrangements.any { it.id == arrangementId && it.songId == songId })
            update("arrangements", arrangementId, "name" to cleanName, "arranger" to text(arranger), "voicing" to text(voicing))
            arrangementId
        }
        update("songs", songId, "arrangement_id" to selected)
        snapshot()
    }
    private fun addArrangement(songId: String, name: String, arranger: String, voicing: String): String {
        val arrangement = id()
        insert("arrangements", "id" to arrangement, "song_id" to songId, "name" to name, "arranger" to arranger, "voicing" to voicing)
        insert("selections", "id" to arrangement)
        return arrangement
    }
    suspend fun selectSong(songId: String?): Library = change {
        require(songId == null || snapshot().songs.any { it.id == songId })
        update("settings", "main", "song_id" to songId)
        snapshot()
    }
    suspend fun selectArrangement(arrangementId: String): Library = change {
        val arrangement = snapshot().arrangements.single { it.id == arrangementId }
        update("songs", arrangement.songId, "arrangement_id" to arrangementId)
        snapshot()
    }
    suspend fun selectResource(resourceId: String): Library = change {
        val resource = snapshot().resources.single { it.id == resourceId }
        update("selections", resource.arrangementId, (if (resource.kind == ImportKind.SCORE) "score_id" else "audio_id") to resourceId)
        snapshot()
    }

    suspend fun savePosition(scoreId: String?, page: Int, zoom: Float, audioId: String?, positionMs: Long) = change {
        // Origin IDs, never current selection, determine the destination of a checkpoint.
        val current = snapshot()
        current.resources.find { it.id == scoreId && it.kind == ImportKind.SCORE }?.let {
            update("resource_state", it.id, "page" to page.coerceIn(0, (it.pages - 1).coerceAtLeast(0)), "zoom" to if (zoom >= 2f) 2f else 1f)
        }
        current.resources.find { it.id == audioId && it.kind == ImportKind.AUDIO }?.let {
            update("resource_state", it.id, "position" to positionMs.coerceIn(0, it.durationMs))
        }
    }

    suspend fun importUri(uri: Uri, arrangementId: String, kind: ImportKind, label: String, part: String, inspectAudio: suspend (File) -> Long): Library {
        require(uri.scheme == "content")
        val filename = withContext(Dispatchers.IO) {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) it.getString(0)?.take(120) else null
            } ?: "Imported file"
        }
        return import(arrangementId, kind, label.ifBlank { filename }, part,
            { context.contentResolver.openInputStream(uri) ?: error("File unavailable") }, inspectAudio)
    }

    suspend fun import(arrangementId: String, kind: ImportKind, label: String, part: String,
        open: () -> InputStream, inspectAudio: suspend (File) -> Long): Library = mutex.withLock {
        val cleanLabel = text(label, true)
        val cleanPart = text(part)
        val resourceId = id()
        val destination = file("$resourceId.${kind.extension}")
        val staged = File(directory, "${destination.name}.incoming")
        try {
            withContext(Dispatchers.IO) { copyBounded(open, staged, kind.maxBytes) }
            val pages = if (kind == ImportKind.SCORE) reader.inspect(staged) else 0
            val duration = if (kind == ImportKind.AUDIO) inspectAudio(staged) else 0
            require(kind != ImportKind.AUDIO || duration > 0)
            withContext(Dispatchers.IO) {
                transaction {
                    require(snapshot().arrangements.any { it.id == arrangementId })
                    check(staged.renameTo(destination))
                    addResource(resourceId, arrangementId, kind, cleanLabel, cleanPart, destination.name, pages, duration)
                    snapshot()
                }
            }
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                staged.delete()
                // Do not delete a file if a successful commit raced coroutine cancellation.
                if (db.rawQuery("SELECT id FROM resources WHERE id = ?", arrayOf(resourceId)).use { !it.moveToFirst() }) destination.delete()
            }
        }
    }
    private fun copyBounded(open: () -> InputStream, target: File, max: Long) {
        open().use { input ->
            target.outputStream().use { output ->
                val buffer = ByteArray(16 * 1024)
                var total = 0L
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    require(count > 0) { "Provider returned no data" }
                    total += count
                    require(total <= max) { "Import exceeds size limit" }
                    output.write(buffer, 0, count)
                }
                require(total > 0) { "Empty file" }
                output.fd.sync()
            }
        }
    }
    private fun addResource(resourceId: String, arrangementId: String, kind: ImportKind, label: String, part: String,
        filename: String, pages: Int, duration: Long, page: Int = 0, position: Long = 0) {
        insert("resources", "id" to resourceId, "arrangement_id" to arrangementId, "kind" to kind.name, "label" to label,
            "part" to part, "filename" to filename, "pages" to pages, "duration" to duration)
        insert("resource_state", "id" to resourceId, "page" to page.coerceIn(0, (pages - 1).coerceAtLeast(0)), "zoom" to 1f, "position" to position.coerceIn(0, duration))
        update("selections", arrangementId, (if (kind == ImportKind.SCORE) "score_id" else "audio_id") to resourceId)
    }

    private suspend fun convertWorkspace(inspectAudio: suspend (File) -> Long) {
        if (withContext(Dispatchers.IO) { converted() }) return
        val legacy = WorkspaceStore(context, reader)
        val old = legacy.loadForConversion()
        val copies = mutableListOf<File>()
        val imported = mutableListOf<Resource>()
        val song = id()
        val arrangement = id()
        try {
            for ((legacyId, kind) in listOf(old.scoreId to ImportKind.SCORE, old.audioId to ImportKind.AUDIO)) {
                if (legacyId == null) continue
                val resourceId = id()
                val destination = file("$resourceId.${kind.extension}")
                copies += destination
                withContext(Dispatchers.IO) { copyBounded({ legacy.file(legacyId).inputStream() }, destination, kind.maxBytes) }
                val pages = if (kind == ImportKind.SCORE) reader.inspect(destination) else 0
                val duration = if (kind == ImportKind.AUDIO) inspectAudio(destination) else 0
                require(kind != ImportKind.AUDIO || duration > 0)
                imported += Resource(resourceId, arrangement, kind,
                    (if (kind == ImportKind.SCORE) old.scoreTitle else old.audioTitle).ifBlank { "Converted workspace file" }, "", destination.name,
                    pages, duration, old.page, 1f, old.positionMs)
            }
            withContext(Dispatchers.IO) {
                transaction {
                    // A second activity/store may have converted while these files were validating.
                    if (converted()) return@transaction
                    if (imported.isNotEmpty()) {
                        insert("songs", "id" to song, "title" to "Converted SAS-006 workspace", "composer" to "", "tags" to "")
                        insert("arrangements", "id" to arrangement, "song_id" to song, "name" to "Original workspace", "arranger" to "", "voicing" to "")
                        insert("selections", "id" to arrangement)
                        update("songs", song, "arrangement_id" to arrangement)
                        imported.forEach { addResource(it.id, arrangement, it.kind, it.label, it.part, it.filename, it.pages, it.durationMs, it.page, it.positionMs) }
                        update("settings", "main", "song_id" to song)
                    }
                    update("settings", "main", "converted" to 1)
                }
            }
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                copies.forEach { copy ->
                    if (db.rawQuery("SELECT id FROM resources WHERE filename = ?", arrayOf(copy.name)).use { !it.moveToFirst() }) copy.delete()
                }
            }
        }
    }

    private fun converted(): Boolean = db.rawQuery("SELECT converted FROM settings WHERE id = 'main'", null).use {
        check(it.moveToFirst())
        it.getInt(0) == 1
    }

    private fun snapshot(): Library {
        fun rows(sql: String, read: (android.database.Cursor) -> Unit) = db.rawQuery(sql, null).use { while (it.moveToNext()) read(it) }
        val songs = mutableListOf<Song>()
        rows("SELECT id,title,composer,tags,arrangement_id FROM songs ORDER BY rowid") { songs += Song(it.getString(0), it.getString(1), it.getString(2), it.getString(3), it.getString(4)) }
        val arrangements = mutableListOf<Arrangement>()
        rows("SELECT id,song_id,name,arranger,voicing FROM arrangements ORDER BY rowid") { arrangements += Arrangement(it.getString(0), it.getString(1), it.getString(2), it.getString(3), it.getString(4)) }
        val resources = mutableListOf<Resource>()
        rows("SELECT r.id,arrangement_id,kind,label,part,filename,pages,duration,page,zoom,position FROM resources r JOIN resource_state s ON r.id=s.id ORDER BY r.rowid") {
            resources += Resource(it.getString(0), it.getString(1), ImportKind.valueOf(it.getString(2)), it.getString(3), it.getString(4), it.getString(5), it.getInt(6), it.getLong(7), it.getInt(8), it.getFloat(9), it.getLong(10))
        }
        val selections = mutableMapOf<String, Pair<String?, String?>>()
        rows("SELECT id,score_id,audio_id FROM selections") { selections[it.getString(0)] = it.getString(1) to it.getString(2) }
        var songId: String? = null
        rows("SELECT song_id FROM settings WHERE id='main'") { songId = it.getString(0) }
        return Library(songs, arrangements, resources, songId, selections, conversionFailed)
    }
    override fun close() = helper.close()

    private class Database(context: Context) : SQLiteOpenHelper(context, "library.db", null, 1) {
        override fun onConfigure(db: SQLiteDatabase) { db.setForeignKeyConstraintsEnabled(true) }
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE songs(id TEXT PRIMARY KEY, title TEXT NOT NULL CHECK(length(trim(title))>0), composer TEXT NOT NULL, tags TEXT NOT NULL, arrangement_id TEXT REFERENCES arrangements(id))")
            db.execSQL("CREATE TABLE arrangements(id TEXT PRIMARY KEY, song_id TEXT NOT NULL REFERENCES songs(id), name TEXT NOT NULL, arranger TEXT NOT NULL, voicing TEXT NOT NULL, UNIQUE(song_id,name))")
            db.execSQL("CREATE INDEX arrangement_song ON arrangements(song_id)")
            db.execSQL("CREATE TABLE resources(id TEXT PRIMARY KEY, arrangement_id TEXT NOT NULL REFERENCES arrangements(id), kind TEXT NOT NULL CHECK(kind IN ('SCORE','AUDIO')), label TEXT NOT NULL, part TEXT NOT NULL, filename TEXT NOT NULL UNIQUE, pages INTEGER NOT NULL, duration INTEGER NOT NULL)")
            db.execSQL("CREATE INDEX resource_arrangement ON resources(arrangement_id)")
            db.execSQL("CREATE TABLE resource_state(id TEXT PRIMARY KEY REFERENCES resources(id), page INTEGER NOT NULL, zoom REAL NOT NULL, position INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE selections(id TEXT PRIMARY KEY REFERENCES arrangements(id), score_id TEXT REFERENCES resources(id), audio_id TEXT REFERENCES resources(id))")
            db.execSQL("CREATE TABLE settings(id TEXT PRIMARY KEY, song_id TEXT REFERENCES songs(id), converted INTEGER NOT NULL DEFAULT 0)")
            db.execSQL("INSERT INTO settings(id) VALUES ('main')")
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { error("Explicit library migration required: $oldVersion → $newVersion") }
    }
}
