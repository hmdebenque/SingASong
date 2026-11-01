package com.frenchfrie.singasong.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.frenchfrie.singasong.practice.ImportKind
import com.frenchfrie.singasong.practice.PracticePlayer
import com.frenchfrie.singasong.practice.WorkspaceStore
import com.frenchfrie.singasong.reader.PdfReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LibraryStoreTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context = instrumentation.targetContext
    private val reader = PdfReader(context)
    private lateinit var store: LibraryStore
    @Before fun reset() {
        context.deleteDatabase("library.db")
        File(context.filesDir, "library").deleteRecursively()
        File(context.filesDir, "practice").deleteRecursively()
        store = LibraryStore(context, reader)
    }
    @After fun close() = store.close()
    private suspend fun load() = store.load { PracticePlayer.inspect(context, it) }
    private suspend fun score(arrangement: String, label: String = "Full score", part: String = "") =
        store.import(arrangement, ImportKind.SCORE, label, part, { context.assets.open("demo/score.pdf") }) { 0 }
    private suspend fun audio(arrangement: String, label: String = "Tutti", part: String = "") =
        store.import(arrangement, ImportKind.AUDIO, label, part, { context.assets.open("demo/practice.wav") }) { PracticePlayer.inspect(context, it) }

    @Test fun metadataSearchAndRelationshipsSurviveReopen() = runBlocking {
        load()
        val first = store.saveSong(null, "Étoile", "Fauré", "Noël, fête").song!!
        store.saveArrangement(first.id, null, "Chœur SATB", "Élodie", "SATB")
        val second = store.saveSong(null, "Étoile", "Other composer", "second").song!!
        store.saveSong(null, "Third song", "", "")
        assertTrue(runCatching { store.saveSong(null, "  ", "", "") }.isFailure)
        assertTrue(runCatching { store.saveArrangement(first.id, null, "choeur", "", "") }.isSuccess)
        assertTrue(runCatching { store.saveArrangement(first.id, null, "CHŒUR SATB", "", "") }.isFailure)
        store.saveSong(second.id, "Lumière", "Debussy", "soir")
        store.close(); store = LibraryStore(context, reader)
        val library = load()
        assertEquals(3, library.songs.size)
        assertEquals(listOf(first.id), library.search("ETOILE faure NOEL elodie").map { it.id })
        assertEquals("Étoile", library.search("étoILE").single().title)
        assertEquals(second.id, library.search("LUMIERE debussy soir").single().id)
        assertEquals(3, library.search(" ").size)
        assertTrue(library.search("absent").isEmpty())
        assertEquals(3, library.arrangements.count { it.songId == first.id })
        assertTrue(runCatching { store.saveArrangement("nonexistent", null, "Bad owner", "", "") }.isFailure)
    }

    @Test fun multipleResourcesKeepIndependentPositionsAndPrivateCopies() = runBlocking {
        load()
        val song = store.saveSong(null, "Three parts", "", "").song!!
        val arrangement = song.arrangementId!!
        val full = score(arrangement).score!!
        val alto = score(arrangement, "Alto score", "Alto").score!!
        val tutti = audio(arrangement).audio!!
        val tenor = audio(arrangement, "Tenor take", "Tenor").audio!!
        store.savePosition(full.id, 2, 2f, tutti.id, 8_000)
        store.savePosition(alto.id, 1, 1f, tenor.id, 16_000)
        store.selectResource(full.id); store.selectResource(tutti.id)
        var library = load()
        assertEquals(2, library.score!!.page); assertEquals(2f, library.score!!.zoom)
        assertEquals(8_000, library.audio!!.positionMs)
        val other = store.saveArrangement(song.id, null, "Second arrangement", "", "SA").arrangement!!
        val otherScore = score(other.id).score!!
        store.savePosition(full.id, 1, 2f, tutti.id, 9_000) // Late checkpoint belongs to the old resources only.
        library = load()
        assertEquals(otherScore.id, library.score!!.id); assertEquals(0, library.score!!.page)
        assertNull(library.audio)
        store.selectArrangement(arrangement); store.selectResource(alto.id); store.selectResource(tenor.id)
        store.close(); store = LibraryStore(context, reader)
        library = load()
        assertEquals(5, library.resources.size)
        assertEquals(1, library.score!!.page); assertEquals(16_000, library.audio!!.positionMs)
        assertTrue(library.resources.all { store.file(it.filename).isFile })
        assertTrue(library.resources.all { runCatching { store.file(it.filename).canonicalPath.startsWith(File(context.filesDir, "library").canonicalPath + "/") }.getOrDefault(false) })
        assertTrue(runCatching { store.file("../../outside") }.isFailure)
        // A moved/deleted source is irrelevant to a committed private copy.
        val source = File(context.cacheDir, "temporary-source.pdf")
        context.assets.open("demo/score.pdf").use { input -> source.outputStream().use(input::copyTo) }
        val copied = store.import(arrangement, ImportKind.SCORE, "Copied source", "", { source.inputStream() }) { 0 }.score!!
        source.delete()
        assertEquals(3, reader.inspect(store.file(copied.filename)))
    }

    @Test fun importFailuresAndRejectedCommitRetainEveryCommittedResource() = runBlocking {
        load()
        val arrangement = store.saveSong(null, "Retained", "", "").arrangement!!.id
        val before = score(arrangement)
        val bytes = store.file(before.score!!.filename).readBytes()
        val failedInputs = listOf<() -> InputStream>(
            { instrumentation.context.assets.open("corrupt.pdf") },
            { object : InputStream() { override fun read(): Int = throw IOException("Provider interrupted") } },
            { object : InputStream() {
                override fun read() = 0
                override fun read(buffer: ByteArray, offset: Int, length: Int): Int { buffer.fill(0); return length }
            } },
        )
        failedInputs.forEach { open ->
            assertTrue(runCatching { store.import(arrangement, ImportKind.SCORE, "Bad", "", open) { 0 } }.isFailure)
            assertEquals(before, load())
        }
        assertTrue(runCatching { store.import(arrangement, ImportKind.AUDIO, "Canceled", "", { context.assets.open("demo/practice.wav") }) { throw CancellationException("Interrupted validation") } }.isFailure)
        SQLiteDatabase.openDatabase(context.getDatabasePath("library.db").path, null, SQLiteDatabase.OPEN_READWRITE).use {
            it.execSQL("CREATE TRIGGER reject_resource BEFORE INSERT ON resources BEGIN SELECT RAISE(ABORT, 'test commit failure'); END")
        }
        assertTrue(runCatching { score(arrangement) }.isFailure)
        assertEquals(before, load())
        assertArrayEquals(bytes, store.file(before.score!!.filename).readBytes())
        assertEquals(1, File(context.filesDir, "library").listFiles()!!.size)
    }

    @Test fun workspaceConversionIsIdempotentAndRetainsOriginalsAndPositions() = runBlocking {
        val legacy = WorkspaceStore(context, reader)
        val score = legacy.import(ImportKind.SCORE, "My old score", { context.assets.open("demo/score.pdf") }) { 0 }
        val audio = legacy.import(ImportKind.AUDIO, "My old recording", { context.assets.open("demo/practice.wav") }) { PracticePlayer.inspect(context, it) }
        legacy.savePosition(score.scoreId, 2, audio.audioId, 12_000)
        val original = legacy.file(score.scoreId!!).readBytes()
        val competingStore = LibraryStore(context, reader)
        val converted = try {
            coroutineScope {
                listOf(async { load() }, async { competingStore.load { PracticePlayer.inspect(context, it) } }).awaitAll()
            }
        } finally { competingStore.close() }
        assertEquals(converted[0], converted[1])
        var library = converted[0]
        assertEquals(1, library.songs.size)
        assertFalse(library.conversionFailed)
        assertEquals("Converted SAS-006 workspace", library.song!!.title)
        assertEquals(2, library.score!!.page); assertEquals(12_000, library.audio!!.positionMs)
        assertEquals("My old score", library.score!!.label)
        assertArrayEquals(original, store.file(library.score!!.filename).readBytes())
        assertArrayEquals(original, legacy.file(score.scoreId).readBytes())
        store.close(); store = LibraryStore(context, reader)
        assertEquals(library, load())
        library = store.saveSong(null, "New song", "", "")
        assertEquals(library, load()); assertEquals(2, library.songs.size)
    }

    @Test fun missingLegacyFileBlocksConversionWithoutWipeAndRetryWorks() = runBlocking {
        val legacy = WorkspaceStore(context, reader)
        val old = legacy.import(ImportKind.SCORE, "Original", { context.assets.open("demo/score.pdf") }) { 0 }
        val path = legacy.file(old.scoreId!!)
        val bytes = path.readBytes(); path.delete()
        assertTrue(load().conversionFailed)
        val existing = store.saveSong(null, "Usable library", "", "").song!!
        assertEquals(existing, load().songs.single())
        assertTrue(File(context.filesDir, "practice/workspace.json").isFile)
        path.writeBytes(bytes)
        val retried = load()
        assertFalse(retried.conversionFailed); assertEquals(2, retried.songs.size)
        assertEquals(1, retried.resources.size)
        assertEquals(retried, load()); assertTrue(path.isFile)
    }

    @Test fun rejectedConversionCommitKeepsOriginalAndRetriesExactlyOnce() = runBlocking {
        val legacy = WorkspaceStore(context, reader)
        val original = legacy.import(ImportKind.SCORE, "Original", { context.assets.open("demo/score.pdf") }) { 0 }
        store.saveSong(null, "Existing library", "", "") // Create schema, leave conversion pending.
        SQLiteDatabase.openDatabase(context.getDatabasePath("library.db").path, null, SQLiteDatabase.OPEN_READWRITE).use {
            it.execSQL("CREATE TRIGGER reject_conversion BEFORE INSERT ON resources BEGIN SELECT RAISE(ABORT, 'test conversion failure'); END")
        }
        var library = load()
        assertTrue(library.conversionFailed); assertEquals(1, library.songs.size); assertTrue(library.resources.isEmpty())
        assertTrue(legacy.file(original.scoreId!!).isFile)
        assertTrue(File(context.filesDir, "library").listFiles()!!.isEmpty())
        SQLiteDatabase.openDatabase(context.getDatabasePath("library.db").path, null, SQLiteDatabase.OPEN_READWRITE).use { it.execSQL("DROP TRIGGER reject_conversion") }
        library = load()
        assertFalse(library.conversionFailed); assertEquals(2, library.songs.size); assertEquals(1, library.resources.size)
        assertEquals(library, load())
    }

    @Test fun malformedLegacyAndFutureSchemaNeverResetStoredContent() = runBlocking {
        File(context.filesDir, "practice").mkdirs()
        val metadata = File(context.filesDir, "practice/workspace.json").apply { writeText("{bad json") }
        assertTrue(load().conversionFailed)
        assertEquals("{bad json", metadata.readText())
        store.saveSong(null, "Keep me", "", "")
        store.close()
        SQLiteDatabase.openDatabase(context.getDatabasePath("library.db").path, null, SQLiteDatabase.OPEN_READWRITE).use { it.version = 2 }
        store = LibraryStore(context, reader)
        assertTrue(runCatching { load() }.isFailure)
        SQLiteDatabase.openDatabase(context.getDatabasePath("library.db").path, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            db.rawQuery("SELECT title FROM songs", null).use { assertTrue(it.moveToFirst()); assertEquals("Keep me", it.getString(0)) }
        }
    }
}
