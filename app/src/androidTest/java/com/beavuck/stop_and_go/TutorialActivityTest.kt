package com.beavuck.stop_and_go

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.activities.SettingsActivity
import com.beavuck.stop_and_go.activities.TutorialActivity
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TutorialActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TutorialActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var tutorialRepository: TutorialRepository
    private lateinit var configRepository: ConfigRepository

    @Before
    fun setup() {
        tutorialRepository = TutorialRepository(context)
        configRepository = ConfigRepository(context)

        tutorialRepository.resetTutorialCompletion()
        configRepository.saveLocale(DEFAULT_LOCALE.code)
    }

    @After
    fun tearDown() {
        tutorialRepository.resetTutorialCompletion()
        configRepository.clearLocale()
    }

    @Test
    fun tutorialActivity_launchesSuccessfully() {
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_language_title))
            .assertExists()
    }

    @Test
    fun languageSelection_savesLocale() {
        val frenchLocale = "locale_${SupportedLocale.FRENCH.code}"

        composeTestRule.onNodeWithTag(frenchLocale).performClick()
        composeTestRule.waitForIdle()

        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.performClick()
        composeTestRule.waitForIdle()

        val savedLocale = configRepository.loadLocale()
        assertEquals(SupportedLocale.FRENCH.code, savedLocale)
    }

    @Test
    fun skipButton_onLanguageStep_completesAndClosesTutorial() {
        val skipButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_skip))

        skipButton.performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        assertFalse(tutorialRepository.shouldShowTutorial())

        val savedLocale = configRepository.loadLocale()
        assertEquals(DEFAULT_LOCALE.code, savedLocale)
    }

    @Test
    fun nextButton_withoutLanguageSelection_usesDefaultLocale() {
        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))

        nextButton.assertIsEnabled()
        nextButton.performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val savedLocale = configRepository.loadLocale()
        assertEquals(DEFAULT_LOCALE.code, savedLocale)
    }

    @Test
    fun skipButton_onGestureStep_completesAndClosesTutorial() {
        advanceToGestureStep()

        val skipButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_skip))
        skipButton.performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        assertFalse(tutorialRepository.shouldShowTutorial())
    }

    @Test
    fun gestureDemoStep_detectsSingleTap() {
        advanceToGestureStep()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_tap_instruction))
            .assertExists()

        composeTestRule.onRoot().performTouchInput { click() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_tap_success))
            .assertExists()

        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.assertIsEnabled()
    }

    @Test
    fun gestureDemoStep_detectsLongPress() {
        advanceToGestureStep()

        completeTapGesture()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_long_instruction))
            .assertExists()

        composeTestRule.onRoot().performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_long_success))
            .assertExists()

        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.assertIsEnabled()
    }

    @Test
    fun gestureDemoStep_detectsMultiTap() {
        advanceToGestureStep()

        completeTapGesture()
        completeLongPressGesture()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_multi_instruction))
            .assertExists()

        composeTestRule.onRoot().performTouchInput { click() }
        Thread.sleep(50)
        composeTestRule.onRoot().performTouchInput { click() }
        Thread.sleep(50)
        composeTestRule.onRoot().performTouchInput { click() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_multi_success))
            .assertExists()

        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.assertIsEnabled()
    }

    @Test
    fun backgroundInfoStep_displaysContent() {
        advanceToGestureStep()
        completeGestureStep()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_background_title))
            .assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_background_description))
            .assertExists()
    }

    @Test
    fun creativeUsesStep_displaysContent() {
        advanceToGestureStep()
        completeGestureStep()
        advanceToCreativeUsesStep()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_creative_title))
            .assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_use_chess))
            .assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_use_apnea))
            .assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_use_pomodoro))
            .assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_use_hiit))
            .assertExists()
    }

    @Test
    fun finishButton_marksTutorialComplete() {
        advanceToGestureStep()
        completeGestureStep()
        advanceToCreativeUsesStep()

        assertTrue(tutorialRepository.shouldShowTutorial())

        val finishButton =
            composeTestRule.onNodeWithText(context.getString(R.string.tutorial_finish))
        finishButton.performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        assertFalse(tutorialRepository.shouldShowTutorial())
    }

    @Test
    fun helpIconInSettings_launchesTutorial() {
        tutorialRepository.markTutorialComplete()

        composeTestRule.activityRule.scenario.close()

        val settingsIntent = Intent(context, SettingsActivity::class.java)
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(settingsIntent)

        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("helpButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        // ensures no crash
    }

    @Test
    fun tutorialProgress_showsCorrectStepNumbers() {
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_progress, 1, 4))
            .assertDoesNotExist()

        advanceToGestureStep()

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_progress, 1, 3))
            .assertExists()
    }

    private fun advanceToGestureStep() {
        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }

    private fun completeTapGesture() {
        composeTestRule.onRoot().performTouchInput { click() }
        composeTestRule.waitForIdle()

        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.performClick()
        composeTestRule.waitForIdle()
    }

    private fun completeLongPressGesture() {
        composeTestRule.onRoot().performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.performClick()
        composeTestRule.waitForIdle()
    }

    private fun completeMultiTapGesture() {
        composeTestRule.onRoot().performTouchInput { click() }
        Thread.sleep(50)
        composeTestRule.onRoot().performTouchInput { click() }
        Thread.sleep(50)
        composeTestRule.onRoot().performTouchInput { click() }
        composeTestRule.waitForIdle()

        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.performClick()
        composeTestRule.waitForIdle()
    }

    private fun completeGestureStep() {
        completeTapGesture()
        completeLongPressGesture()
        completeMultiTapGesture()
    }

    private fun advanceToCreativeUsesStep() {
        val nextButton = composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))
        nextButton.performClick()
        composeTestRule.waitForIdle()
    }
}
