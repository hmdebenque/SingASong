package com.frenchfrie.singasong

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.frenchfrie.singasong.practice.PracticeViewModel
import java.io.File
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoundationTest {
    @get:Rule
    val compose = createEmptyComposeRule()
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun launchEmpty() {
        File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "practice").deleteRecursively()
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase("library.db")
        File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "library").deleteRecursively()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        compose.waitUntil(15_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasTestTag("load-demo")).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @After
    fun close() = scenario.close()

    @Test
    fun launcherShowsLibraryAndSurvivesRecreation() {
        compose.onNodeWithText("Your choir repertoire").assertIsDisplayed()
        scenario.recreate()
        compose.onNodeWithTag("load-demo").assertIsDisplayed()
    }

    @Test
    fun demoRendersPlaysSeeksAndRetainsIndependentPageAcrossRecreation() {
        compose.onNodeWithTag("load-demo").performClick()
        compose.waitUntil(30_000) { snapshot()?.ready == true && snapshot()?.bitmap != null }
        compose.onNodeWithTag("score-image").assertIsDisplayed()
        compose.onNodeWithTag("play-pause").performClick()
        compose.waitUntil(10_000) { snapshot()?.playing == true && (snapshot()?.positionMs ?: 0) > 750 }
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithTag("page-count").assertTextContains("2 / 3")
        assertTrue("Turning the score page preserves recording time", snapshot()!!.positionMs > 750)
        compose.onNodeWithTag("seek").performSemanticsAction(SemanticsActions.SetProgress) { it(8_000f) }
        compose.onNodeWithTag("play-pause").performClick()
        compose.waitUntil(5_000) { snapshot()?.playing == false && (snapshot()?.positionMs ?: 0) >= 8_000 }
        val before = snapshot()!!
        scenario.recreate()
        compose.waitUntil(15_000) { snapshot()?.bitmap != null }
        compose.onNodeWithTag("page-count").assertTextContains("2 / 3")
        assertEquals(before.workspace.scoreId, snapshot()!!.workspace.scoreId)
        assertTrue(snapshot()!!.positionMs >= 8_000)
        assertFalse(snapshot()!!.playing)
        scenario.close()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        compose.waitUntil(20_000) { snapshot()?.ready == true && snapshot()?.bitmap != null }
        assertEquals(1, snapshot()!!.workspace.page)
        assertTrue(snapshot()!!.positionMs >= 8_000)
        assertFalse("Cold start never autoplays", snapshot()!!.playing)
    }

    @Test
    fun playbackPausesOnBackgroundAndAudioFocusLoss() {
        compose.onNodeWithTag("load-demo").performClick()
        compose.waitUntil(30_000) { snapshot()?.ready == true }
        compose.onNodeWithTag("play-pause").performClick()
        compose.waitUntil(10_000) { snapshot()?.playing == true && (snapshot()?.positionMs ?: 0) > 750 }
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED)
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.waitUntil(5_000) { snapshot()?.playing == false }
        assertTrue(snapshot()!!.positionMs > 0)
        compose.onNodeWithTag("play-pause").performClick()
        compose.waitUntil(5_000) { snapshot()?.playing == true }
        var request: android.media.AudioFocusRequest? = null
        scenario.onActivity { activity ->
            val manager = activity.getSystemService(android.media.AudioManager::class.java)
            request = android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA).build())
                .setOnAudioFocusChangeListener({ }, android.os.Handler(android.os.Looper.getMainLooper()))
                .build()
            assertEquals(android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED, manager.requestAudioFocus(request!!))
        }
        try {
            compose.waitUntil(5_000) { snapshot()?.playing == false }
        } finally {
            scenario.onActivity { it.getSystemService(android.media.AudioManager::class.java).abandonAudioFocusRequest(request!!) }
        }
    }


    @Test
    fun songFormsValidateCancelEditAndSearchThreeSongs() {
        compose.onNodeWithTag("new-song").performClick()
        compose.onNodeWithTag("save-edit").performClick()
        compose.onNodeWithTag("validation").assertIsDisplayed()
        compose.onNodeWithTag("edit-name").performTextInput("Étoile")
        compose.onNodeWithTag("edit-detail").performTextInput("Fauré")
        compose.onNodeWithTag("edit-extra").performTextInput("Noël")
        compose.onNodeWithTag("save-edit").performScrollTo().performClick()
        compose.waitUntil(5_000) { snapshot()?.library?.song?.title == "Étoile" }
        compose.onNodeWithText("Edit song").performClick()
        compose.onNodeWithTag("edit-name").performTextReplacement("Canceled change")
        compose.onNodeWithText("Cancel").performScrollTo().performClick()
        assertEquals("Étoile", snapshot()!!.library.song!!.title)
        compose.onNodeWithText("Edit song").performClick()
        compose.onNodeWithTag("edit-name").performTextReplacement("Étoile du soir")
        compose.onNodeWithTag("save-edit").performScrollTo().performClick()
        compose.waitUntil(5_000) { snapshot()?.library?.song?.title == "Étoile du soir" }
        for (name in listOf("Second", "Third")) {
            compose.onNodeWithText("Library").performClick()
            compose.waitUntil(5_000) { snapshot()?.library?.song == null && snapshot()?.importing == false }
            compose.onNodeWithTag("new-song").performClick()
            compose.onNodeWithTag("edit-name").performTextInput(name)
            compose.onNodeWithTag("save-edit").performScrollTo().performClick()
            compose.waitUntil(5_000) { snapshot()?.library?.song?.title == name }
        }
        compose.onNodeWithText("Library").performClick()
        compose.waitUntil(5_000) { snapshot()?.library?.song == null && snapshot()?.importing == false }
        compose.onNodeWithTag("search").performTextInput("ETOILE faure noel")
        compose.onNodeWithText("Étoile du soir").assertIsDisplayed()
        compose.onNodeWithText("Second").assertDoesNotExist()
        compose.onNodeWithTag("search").performTextReplacement("Missing")
        compose.onNodeWithText("No matching songs. Try another search.").assertIsDisplayed()
        compose.onNodeWithTag("search").performTextReplacement("")
        scenario.recreate()
        assertEquals(3, snapshot()!!.library.songs.size)
    }

    @Test
    fun switchingSongsAndResourcesPausesAndRestoresTheirOwnState() {
        compose.onNodeWithTag("load-demo").performClick()
        compose.waitUntil(30_000) { snapshot()?.ready == true && snapshot()?.bitmap != null }
        val demo = snapshot()!!.library.song!!.id
        val score = snapshot()!!.workspace.scoreId!!
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Zoom 2×").performClick()
        compose.onNodeWithTag("seek").performSemanticsAction(SemanticsActions.SetProgress) { it(8_000f) }
        compose.onNodeWithTag("play-pause").performClick()
        compose.waitUntil(5_000) { snapshot()?.playing == true }
        scenario.onActivity { ViewModelProvider(it)[PracticeViewModel::class.java].saveSong(null, "Another song", "", "") }
        compose.waitUntil(10_000) { snapshot()?.library?.song?.title == "Another song" && snapshot()?.importing == false }
        assertFalse(snapshot()!!.playing); assertFalse(snapshot()!!.ready); assertNull(snapshot()!!.bitmap)
        assertNull(snapshot()!!.workspace.scoreId); assertNull(snapshot()!!.workspace.audioId)
        scenario.onActivity { ViewModelProvider(it)[PracticeViewModel::class.java].openSong(demo) }
        compose.waitUntil(20_000) { snapshot()?.ready == true && snapshot()?.bitmap != null && snapshot()?.importing == false }
        assertEquals(score, snapshot()!!.workspace.scoreId); assertEquals(1, snapshot()!!.workspace.page)
        assertEquals(2f, snapshot()!!.zoom); assertTrue(snapshot()!!.positionMs >= 8_000); assertFalse(snapshot()!!.playing)
        compose.onNodeWithTag("materials").performClick()
        compose.onNodeWithText("New arrangement").performScrollTo().performClick()
        compose.onNodeWithTag("edit-name").performTextInput("Women's choir")
        compose.onNodeWithTag("edit-detail").performTextInput("Élodie")
        compose.onNodeWithTag("save-edit").performScrollTo().performClick()
        compose.waitUntil(5_000) { snapshot()?.library?.arrangement?.name == "Women's choir" && snapshot()?.importing == false }
        assertNull(snapshot()!!.workspace.scoreId); assertFalse(snapshot()!!.playing)
        compose.onNodeWithTag("materials").performClick()
        compose.onNodeWithText("Default arrangement").performScrollTo().performClick()
        compose.waitUntil(15_000) { snapshot()?.workspace?.scoreId == score && snapshot()?.importing == false }
        compose.onNodeWithText("Close").performScrollTo().performClick()
        assertEquals(1, snapshot()!!.workspace.page)
    }

    @Test
    fun selectingLabelledPdfAndRecordingRestoresResourceSpecificState() = kotlinx.coroutines.runBlocking {
        compose.onNodeWithTag("load-demo").performClick()
        compose.waitUntil(30_000) { snapshot()?.ready == true && snapshot()?.bitmap != null }
        val original = snapshot()!!
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = com.frenchfrie.singasong.library.LibraryStore(context, com.frenchfrie.singasong.reader.PdfReader(context))
        val arrangement = original.library.arrangement!!.id
        val part = store.import(arrangement, com.frenchfrie.singasong.practice.ImportKind.SCORE, "Alto PDF", "Alto", { context.assets.open("demo/score.pdf") }) { 0 }.score!!
        val take = store.import(arrangement, com.frenchfrie.singasong.practice.ImportKind.AUDIO, "Alto take", "Alto", { context.assets.open("demo/practice.wav") }) {
            com.frenchfrie.singasong.practice.PracticePlayer.inspect(context, it)
        }.audio!!
        store.savePosition(part.id, 2, 2f, take.id, 16_000)
        store.selectResource(original.workspace.scoreId!!); store.selectResource(original.workspace.audioId!!)
        store.close()
        scenario.onActivity { ViewModelProvider(it)[PracticeViewModel::class.java].retryRestore() }
        compose.waitUntil(10_000) { snapshot()?.importing == false }
        compose.onNodeWithTag("seek").performSemanticsAction(SemanticsActions.SetProgress) { it(8_000f) }
        compose.onNodeWithTag("play-pause").performClick()
        compose.waitUntil(5_000) { snapshot()?.playing == true }
        compose.onNodeWithTag("materials").performClick()
        compose.onNodeWithTag("resource-${part.id}").performScrollTo().performClick()
        compose.waitUntil(10_000) { snapshot()?.workspace?.scoreId == part.id && snapshot()?.importing == false }
        assertFalse(snapshot()!!.playing); assertEquals(2, snapshot()!!.workspace.page); assertEquals(2f, snapshot()!!.zoom)
        assertTrue(snapshot()!!.positionMs >= 8_000) // PDF selection keeps the same recording's time.
        compose.onNodeWithTag("resource-${take.id}").performScrollTo().performClick()
        compose.waitUntil(10_000) { snapshot()?.workspace?.audioId == take.id && snapshot()?.ready == true && snapshot()?.importing == false }
        assertEquals(16_000, snapshot()!!.positionMs); assertEquals(2, snapshot()!!.workspace.page); assertFalse(snapshot()!!.playing)
        compose.onNodeWithText("Close").performScrollTo().performClick()
        compose.onNodeWithTag("page-count").assertTextContains("3 / 3")
        scenario.recreate()
        assertEquals(part.id, snapshot()!!.workspace.scoreId); assertEquals(take.id, snapshot()!!.workspace.audioId)
    }

    private fun snapshot(): com.frenchfrie.singasong.practice.PracticeState? {
        var result: com.frenchfrie.singasong.practice.PracticeState? = null
        scenario.onActivity { result = ViewModelProvider(it)[PracticeViewModel::class.java].state.value }
        return result
    }
}
