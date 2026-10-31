package com.frenchfrie.singasong

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

    private fun snapshot(): com.frenchfrie.singasong.practice.PracticeState? {
        var result: com.frenchfrie.singasong.practice.PracticeState? = null
        scenario.onActivity { result = ViewModelProvider(it)[PracticeViewModel::class.java].state.value }
        return result
    }
}
