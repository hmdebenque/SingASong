package com.frenchfrie.singasong.practice

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frenchfrie.singasong.reader.PdfReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkspaceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context = instrumentation.targetContext
    private val reader = PdfReader(context)
    private lateinit var store: WorkspaceStore

    @Before
    fun reset() {
        File(context.filesDir, "practice").deleteRecursively()
        store = WorkspaceStore(context, reader)
    }

    private suspend fun score() = store.import(ImportKind.SCORE, "Generated score", { context.assets.open("demo/score.pdf") }) { 0 }
    private suspend fun audio() = store.import(ImportKind.AUDIO, "Generated audio", { context.assets.open("demo/practice.wav") }) {
        PracticePlayer.inspect(context, it)
    }

    @Test
    fun importsRenderDecodeAndRestoreWithoutSourceUris() = runBlocking {
        val score = score()
        assertEquals(3, score.pageCount)
        val audio = audio()
        assertTrue(audio.durationMs in 31_900..32_100)
        val bitmap = reader.render(store.file(score.scoreId!!), 2, 1200)
        assertTrue(bitmap.width <= 2048 && bitmap.height <= 2048)
        var dark = 0
        for (x in 0 until bitmap.width step 8) for (y in 0 until bitmap.height step 8) {
            if (Color.red(bitmap.getPixel(x, y)) < 100) dark++
        }
        assertTrue("Real score marks were rendered", dark > 20)
        bitmap.recycle()
        store.savePosition(score.scoreId, 2, audio.audioId, 8_000)
        val restored = WorkspaceStore(context, reader).load()
        assertEquals(score.scoreId, restored.scoreId)
        assertEquals(audio.audioId, restored.audioId)
        assertEquals(2, restored.page)
        assertEquals(8_000, restored.positionMs)
    }

    @Test
    fun corruptAndInterruptedImportsKeepPreviousFiles() = runBlocking {
        val previous = score()
        val previousBytes = store.file(previous.scoreId!!).readBytes()
        for (open in listOf<() -> InputStream>(
            { instrumentation.context.assets.open("corrupt.pdf") },
            { ByteArrayInputStream(byteArrayOf()) },
            { object : InputStream() { override fun read(): Int = throw IOException("Interrupted provider") } },
        )) {
            assertTrue(runCatching { store.import(ImportKind.SCORE, "bad", open) { 0 } }.isFailure)
            assertEquals(previous, store.load())
            assertArrayEquals(previousBytes, store.file(previous.scoreId).readBytes())
        }
        assertFalse(File(context.filesDir, "practice").listFiles()!!.any { it.name.endsWith(".incoming") })
    }

    @Test
    fun oversizeInputRejectedBeforeCommitAndNamesCannotEscape() = runBlocking {
        val previous = score()
        val huge = object : InputStream() {
            override fun read() = 0
            override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
                buffer.fill(0, offset, offset + length)
                return length
            }
        }
        assertTrue(runCatching { store.import(ImportKind.SCORE, "../../score.pdf", { huge }) { 0 } }.isFailure)
        assertEquals(previous, store.load())
        assertTrue(runCatching { store.file("../../outside") }.isFailure)
        assertTrue(runCatching { store.importUri(Uri.parse("file:///private"), ImportKind.SCORE) { 0 } }.isFailure)
    }

    @Test
    fun scanLikeAndExtremePageDimensionsUseBoundedBitmaps() = runBlocking {
        for (name in listOf("scan-like.pdf", "oversized.pdf")) {
            val state = store.import(ImportKind.SCORE, name, { instrumentation.context.assets.open(name) }) { 0 }
            val bitmap = reader.render(store.file(state.scoreId!!), 0, 100_000)
            assertTrue(bitmap.width in 1..2048 && bitmap.height in 1..2048)
            assertEquals(1, state.pageCount)
            bitmap.recycle()
        }
    }

    @Test
    fun mp3DecodesAndCorruptRecordingDoesNotReplaceIt() = runBlocking {
        val valid = store.import(ImportKind.AUDIO, "Generated MP3", { instrumentation.context.assets.open("practice.mp3") }) {
            PracticePlayer.inspect(context, it)
        }
        assertTrue(valid.durationMs > 30_000)
        assertTrue(runCatching {
            store.import(ImportKind.AUDIO, "bad", { instrumentation.context.assets.open("corrupt.wav") }) {
                PracticePlayer.inspect(context, it)
            }
        }.isFailure)
        assertEquals(valid, store.load())
    }

    @Test
    fun abandonedImportIsCleanedAndStalePositionCannotAlterReplacement() = runBlocking {
        val previous = score()
        val abandoned = File(context.filesDir, "practice/abandoned.incoming").apply { writeText("unfinished") }
        val restored = WorkspaceStore(context, reader).load()
        assertEquals(previous, restored)
        assertFalse(abandoned.exists())
        val replacement = score()
        store.savePosition(previous.scoreId, 2, null, 0)
        assertEquals(replacement, store.load())
        assertFalse(store.file(previous.scoreId!!).exists())
    }
}
