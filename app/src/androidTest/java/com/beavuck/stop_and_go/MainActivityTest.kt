package com.beavuck.stop_and_go

import android.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.activities.MainActivity
import com.beavuck.stop_and_go.model.AppState
import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.INITIAL_CYCLE_COUNT
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.pow

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var stateRepository: StateRepository
    private lateinit var configRepository: ConfigRepository
    private var manuallyLaunchedScenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        stateRepository = StateRepository(context)
        configRepository = ConfigRepository(context)
        stateRepository.clearState()
        val appState = AppState()
        stateRepository.saveState(appState)
        stateRepository.setResetPending(false)
        configRepository.saveConfig(TimerConfig())

        val tutorialRepository = TutorialRepository(context)
        tutorialRepository.markTutorialComplete()
    }

    @After
    fun tearDown() {
        manuallyLaunchedScenario?.close()
        manuallyLaunchedScenario = null
        stateRepository.clearState()
        configRepository.saveConfig(TimerConfig())
    }

    @Test
    fun activityLaunches_successfully() {
        composeTestRule.onNodeWithTag("timerDisplay").assertExists()
    }

    @Test
    fun timerText_isDisplayed() {
        composeTestRule.onNodeWithTag("timerText").assertIsDisplayed()
    }

    @Test
    fun cycleCountText_isDisplayed() {
        composeTestRule.onNodeWithTag("cycleCount").assertIsDisplayed()
    }

    @Test
    fun phaseLabel_isDisplayed() {
        composeTestRule.onNodeWithTag("phaseLabel").assertIsDisplayed()
    }

    @Test
    fun initialPhaseLabel_showsGo() {
        val expectedLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(expectedLabel)
    }

    @Test
    fun initialCycleCount_showsOne() {
        val expectedText = context.getString(R.string.cycle_count, INITIAL_CYCLE_COUNT + 1)
        composeTestRule.onNodeWithTag("cycleCount")
            .assertTextEquals(expectedText)
    }

    @Test
    fun timerText_changesAfterOneSecond() {
        composeTestRule.waitForIdle()
        tapTimer()
        Thread.sleep(1500)

        val locale = java.util.Locale.getDefault()
        val initialText =
            java.text.NumberFormat.getIntegerInstance(locale).format(DEFAULT_GO_DURATION)
        composeTestRule.onNodeWithTag("timerText")
            .assert(hasTextExactly(initialText).not())
    }

    @Test
    fun timerText_countdownsCorrectly() {
        composeTestRule.waitForIdle()
        tapTimer()
        Thread.sleep(1500)

        val locale = java.util.Locale.getDefault()
        val expectedText =
            java.text.NumberFormat.getIntegerInstance(locale).format(DEFAULT_GO_DURATION - 1)
        composeTestRule.onNodeWithTag("timerText")
            .assertTextEquals(expectedText)
    }

    @Test
    fun activityRecreation_maintainsTimerState() {
        composeTestRule.waitForIdle()
        tapTimer()
        Thread.sleep(1500)

        val timerValueBefore = getTimerValue()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val timerValueAfter = getTimerValue()

        assertTrue(
            "Timer value should be preserved across recreation",
            timerValueAfter in (timerValueBefore - 2)..(timerValueBefore + 1)
        )
    }

    @Test
    fun activityLaunch_restoresStopPhase() {
        composeTestRule.activityRule.scenario.close()

        val stopPhaseState = AppState(
            cycleCount = 1,
            isGo = false,
            secondsRemaining = 10,
        )
        stateRepository.saveState(stopPhaseState)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val expectedLabel = context.getString(R.string.phase_stop)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(expectedLabel)

        val timerValue = getTimerValue()

        assertTrue("Timer should show remaining time", timerValue in 8..10)
    }

    @Test
    fun timerRunning_keepsScreenOn() {
        tapTimer()
        composeTestRule.waitForIdle()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            val flags = activity.window.attributes.flags
            val keepScreenOn = flags and android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            assertTrue("FLAG_KEEP_SCREEN_ON should be set when timer is running", keepScreenOn != 0)
        }
    }

    @Test
    fun textColor_contrastsWithLightBackground() {
        composeTestRule.activityRule.scenario.close()

        val lightConfig = TimerConfig(goColor = "#FFFFFF", stopColor = "#F0F0F0")
        configRepository.saveConfig(lightConfig)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val backgroundColor = Color.parseColor("#FFFFFF")
        val expectedTextColor = ColorUtils.getContrastingTextColor(backgroundColor)

        assertTrue(
            "Text should be dark on light background",
            Color.luminance(expectedTextColor) < 0.5f
        )
    }

    @Test
    fun textColor_contrastsWithDarkBackground() {
        composeTestRule.activityRule.scenario.close()

        val darkConfig = TimerConfig(goColor = "#000000", stopColor = "#202020")
        configRepository.saveConfig(darkConfig)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val backgroundColor = Color.parseColor("#000000")
        val expectedTextColor = ColorUtils.getContrastingTextColor(backgroundColor)

        assertTrue(
            "Text should be light on dark background",
            Color.luminance(expectedTextColor) > 0.5f
        )
    }

    @Test
    fun timerFinish_transitionsFromGoToStop() {
        composeTestRule.activityRule.scenario.close()

        stateRepository.clearState()
        val shortConfig = TimerConfig(goDuration = 2, stopDuration = 2)
        configRepository.saveConfig(shortConfig)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val initialPhaseLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(initialPhaseLabel)

        composeTestRule.waitForIdle()
        tapTimer()

        Thread.sleep(3000)

        val stopPhaseLabel = context.getString(R.string.phase_stop)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(stopPhaseLabel)
    }

    @Test
    fun timerScreen_hasContentDescription() {
        val screenDescription = context.getString(R.string.timer_screen)
        composeTestRule.onNodeWithTag("timerDisplay")
            .assert(hasContentDescription(screenDescription))
    }

    @Test
    fun activityLaunch_withInvalidColorInConfig_doesNotCrash() {
        composeTestRule.activityRule.scenario.close()

        val invalidColorConfig = TimerConfig(goColor = "#pppppp", stopColor = "#xxxxxx")
        configRepository.saveConfig(invalidColorConfig)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("timerDisplay").assertExists()
        composeTestRule.onNodeWithTag("timerText").assertIsDisplayed()
    }

    @Test
    fun timerScreen_hasPauseAction_whenRunning() {
        composeTestRule.onNodeWithTag("timerDisplay")
            .assert(hasClickAction())
    }

    @Test
    fun timerScreen_hasSettingsAction() {
        composeTestRule.onNodeWithTag("timerDisplay")
            .assertHasClickAction()
    }

    @Test
    fun timer_continuesCountingThroughMultiplePhases() {
        composeTestRule.activityRule.scenario.close()

        stateRepository.clearState()
        val shortConfig = TimerConfig(goDuration = 3, stopDuration = 3)
        configRepository.saveConfig(shortConfig)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val goPhaseLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(goPhaseLabel)

        composeTestRule.waitForIdle()
        tapTimer()

        Thread.sleep(3500)

        val stopPhaseLabel = context.getString(R.string.phase_stop)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(stopPhaseLabel)

        val timerValueInStop = getTimerValue()

        assertTrue("Timer should be counting down in Stop phase", timerValueInStop in 0..3)

        Thread.sleep(3500)

        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(goPhaseLabel)

        val timerValueInSecondGo = getTimerValue()

        assertTrue("Timer should be counting down in second Go phase", timerValueInSecondGo in 0..3)
    }

    @Test
    fun stateCleared_andActivityRecreated_resetsTimerToInitialState() {
        composeTestRule.activityRule.scenario.close()

        val modifiedState = AppState(
            cycleCount = 5,
            isGo = false,
            currentGoDuration = 100,
            currentStopDuration = 80,
            secondsRemaining = 42,
        )
        stateRepository.saveState(modifiedState)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val stopPhaseLabel = context.getString(R.string.phase_stop)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(stopPhaseLabel)

        val cycleBeforeReset = getCycleCount()

        assertTrue("Cycle count should be 6 before reset", cycleBeforeReset.contains("6"))

        stateRepository.clearState()

        manuallyLaunchedScenario?.recreate()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val goPhaseLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(goPhaseLabel)

        val cycleAfterReset = context.getString(R.string.cycle_count, INITIAL_CYCLE_COUNT + 1)
        composeTestRule.onNodeWithTag("cycleCount")
            .assertTextEquals(cycleAfterReset)

        assertTrue(
            "Timer should reset to initial duration when state is cleared",
            getTimerValue() in (DEFAULT_GO_DURATION - 1)..DEFAULT_GO_DURATION
        )
    }

    @Test
    fun tripleTap_resetsTimerToInitialState() {
        composeTestRule.waitForIdle()

        tapTimer()

        Thread.sleep(3000)

        val timerBeforeReset = getTimerValue()

        assertTrue("Timer should have counted down", timerBeforeReset in 56..59)

        tapTimer()
        Thread.sleep(50)
        tapTimer()
        Thread.sleep(50)
        tapTimer()

        composeTestRule.waitForIdle()
        Thread.sleep(100)

        val goPhaseLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(goPhaseLabel)

        val cycleAfterReset = context.getString(R.string.cycle_count, INITIAL_CYCLE_COUNT + 1)
        composeTestRule.onNodeWithTag("cycleCount")
            .assertTextEquals(cycleAfterReset)

        val timerAfterReset = getTimerValue()

        assertTrue(
            "Timer should reset to initial duration after triple tap",
            timerAfterReset in 59..60
        )
    }

    @Test
    fun singleTap_togglesPause() {
        composeTestRule.onNodeWithTag("pauseIcon").assertIsDisplayed()

        tapTimer()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pauseIcon").assertDoesNotExist()

        Thread.sleep(600) // large enough to avoid being detected as part of a triple tap

        tapTimer()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pauseIcon").assertIsDisplayed()
    }

    @Test
    fun pauseIcon_isDisplayed_whenTimerPaused() {
        composeTestRule.onNodeWithTag("pauseIcon").assertIsDisplayed()
    }

    @Test
    fun pauseOverlay_isDisplayed_whenTimerPaused() {
        composeTestRule.onNodeWithTag("pauseOverlay").assertExists()
    }

    @Test
    fun pauseIcon_isNotDisplayed_whenTimerRunning() {
        tapTimer()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pauseIcon").assertDoesNotExist()
    }

    @Test
    fun pauseOverlay_isNotDisplayed_whenTimerRunning() {
        tapTimer()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pauseOverlay").assertDoesNotExist()
    }

    @Test
    fun customGoLabel_isDisplayed_whenConfigured() {
        composeTestRule.activityRule.scenario.close()

        val customConfig = TimerConfig(
            goLabel = "Sprint",
            stopLabel = "Rest"
        )
        configRepository.saveConfig(customConfig)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals("Sprint")
    }

    @Test
    fun customStopLabel_isDisplayed_whenConfigured() {
        composeTestRule.activityRule.scenario.close()

        val customConfig = TimerConfig(
            goDuration = 2,
            stopDuration = 2,
            goLabel = "Run",
            stopLabel = "Walk"
        )
        configRepository.saveConfig(customConfig)
        stateRepository.clearState()

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        tapTimer()

        Thread.sleep(3000)

        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals("Walk")
    }

    @Test
    fun defaultGoLabel_isDisplayed_whenNoCustomLabel() {
        val expectedLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(expectedLabel)
    }

    @Test
    fun emptyLabels_fallBackToDefaultLabels() {
        composeTestRule.activityRule.scenario.close()

        val config = TimerConfig(
            goLabel = "",
            stopLabel = ""
        )
        configRepository.saveConfig(config)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val expectedLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(expectedLabel)
    }

    @Test
    fun topAppBar_settingsButton_isDisplayed() {
        composeTestRule.onNodeWithTag("settingsButton").assertIsDisplayed()
    }

    @Test
    fun topAppBar_resetButton_isDisplayed() {
        composeTestRule.onNodeWithTag("resetTimerButton").assertIsDisplayed()
    }

    @Test
    fun topAppBar_settingsButton_hasCorrectContentDescription() {
        val expectedDescription = context.getString(R.string.settings)
        composeTestRule.onNodeWithTag("settingsButton")
            .assert(hasContentDescription(expectedDescription))
    }

    @Test
    fun topAppBar_resetButton_hasCorrectContentDescription() {
        val expectedDescription = context.getString(R.string.reset_timer)
        composeTestRule.onNodeWithTag("resetTimerButton")
            .assert(hasContentDescription(expectedDescription))
    }

    @Test
    fun topAppBar_resetButton_resetsTimerToInitialState() {
        composeTestRule.waitForIdle()

        tapTimer()

        Thread.sleep(3000)

        val timerBeforeReset = getTimerValue()

        assertTrue("Timer should have counted down", timerBeforeReset in 56..59)

        composeTestRule.onNodeWithTag("resetTimerButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val goPhaseLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(goPhaseLabel)

        val cycleAfterReset = context.getString(R.string.cycle_count, INITIAL_CYCLE_COUNT + 1)
        composeTestRule.onNodeWithTag("cycleCount")
            .assertTextEquals(cycleAfterReset)

        val timerAfterReset = getTimerValue()

        assertTrue(
            "Timer should reset to initial duration after clicking reset button",
            timerAfterReset in 59..60
        )
    }

    @Test
    fun topAppBar_resetButton_resetsCycleCount() {
        composeTestRule.activityRule.scenario.close()

        val modifiedState = AppState(
            cycleCount = 5,
            isGo = false,
            secondsRemaining = 10,
        )
        stateRepository.saveState(modifiedState)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val cycleBeforeReset = getCycleCount()

        assertTrue("Cycle count should be 6 before reset", cycleBeforeReset.contains("6"))

        composeTestRule.onNodeWithTag("resetTimerButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val cycleAfterReset = context.getString(R.string.cycle_count, INITIAL_CYCLE_COUNT + 1)
        composeTestRule.onNodeWithTag("cycleCount")
            .assertTextEquals(cycleAfterReset)
    }

    @Test
    fun app_startsPaused() {
        composeTestRule.onNodeWithTag("pauseIcon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pauseOverlay").assertExists()

        val timerValueBefore = getTimerValue()

        Thread.sleep(1500)

        val timerValueAfter = getTimerValue()

        assertTrue(
            "Timer should not count down when paused on start",
            timerValueAfter == timerValueBefore
        )
    }

    @Test
    fun app_startsPausedWithFullDuration() {
        val timerValue = getTimerValue()

        assertTrue(
            "Timer should start with full duration, not 0",
            timerValue == DEFAULT_GO_DURATION
        )
    }

    @Test
    fun app_startsPausedOnGoPhase() {
        val goPhaseLabel = context.getString(R.string.phase_go)
        composeTestRule.onNodeWithTag("phaseLabel")
            .assertTextEquals(goPhaseLabel)
    }

    @Test
    fun unpauseAfterSettingsSave_doesNotCauseDoubleCountdown() {
        tapTimer()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("settingsButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("saveButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val timerBefore = getTimerValue()

        Thread.sleep(1000)

        val timerAfter = getTimerValue()

        val countdown = timerBefore - timerAfter

        assertTrue(
            "Timer should count down by approximately 1 second, not jump by large amounts. Before: $timerBefore, After: $timerAfter, Diff: $countdown",
            countdown in 0..2
        )
    }

    @Test
    fun resetAfterSettingsSave_doesNotCauseDoubleCountdown() {
        tapTimer()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("settingsButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("saveButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("resetTimerButton").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val timerBefore = getTimerValue()

        Thread.sleep(1000)

        val timerAfter = getTimerValue()

        val countdown = timerBefore - timerAfter

        assertTrue(
            "Timer should count down by approximately 1 second after reset, not jump by large amounts. Before: $timerBefore, After: $timerAfter, Diff: $countdown",
            countdown in 0..2
        )
    }

    @Test
    fun activityRecreation_preservesPausedState() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pauseIcon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pauseOverlay").assertExists()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pauseIcon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pauseOverlay").assertExists()

        val timerValueBefore = getTimerValue()

        Thread.sleep(1500)

        val timerValueAfter = getTimerValue()

        assertTrue(
            "Timer should remain paused after orientation change",
            timerValueAfter == timerValueBefore
        )
    }

    @Test
    fun activityRecreation_preservesRunningState() {
        composeTestRule.waitForIdle()

        tapTimer()
        composeTestRule.waitForIdle()
        Thread.sleep(1500)

        val timerValueBeforeRecreate = getTimerValue()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        val timerValueAfterRecreate = getTimerValue()

        Thread.sleep(1500)

        val timerValueAfterWait = getTimerValue()

        assertTrue(
            "Timer should continue counting down after orientation change. Before recreate: $timerValueBeforeRecreate, After recreate: $timerValueAfterRecreate, After wait: $timerValueAfterWait",
            timerValueAfterWait < timerValueAfterRecreate
        )

        composeTestRule.onNodeWithTag("pauseIcon").assertDoesNotExist()
        composeTestRule.onNodeWithTag("pauseOverlay").assertDoesNotExist()
    }

    private fun getCycleCount(): String = composeTestRule.onNodeWithTag("cycleCount")
        .fetchSemanticsNode()
        .config[SemanticsProperties.Text]
        .first().text

    private fun getTimerValue(): Int = composeTestRule.onNodeWithTag("timerText")
        .fetchSemanticsNode()
        .config[SemanticsProperties.Text]
        .first().text
        .toIntOrNull() ?: 0


    @Test
    fun mainActivity_launchesTutorial_onFirstRun() {
        composeTestRule.activityRule.scenario.close()

        val tutorialRepository = TutorialRepository(context)
        tutorialRepository.resetTutorialCompletion()

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_language_title))
            .assertExists()
    }

    private fun tapTimer() {
        composeTestRule.onNodeWithTag("timerDisplay").performClick()
    }

    @Test
    fun rapidTripleTap_neverShowsTimerExceedingPhaseDuration() {
        composeTestRule.activityRule.scenario.close()

        val goDuration = 60
        val stopDuration = 15
        val config = TimerConfig(goDuration = goDuration, stopDuration = stopDuration)
        configRepository.saveConfig(config)
        stateRepository.clearState()

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val observedValues = mutableListOf<Int>()

        repeat(20) { iteration ->
            val timerValue = getTimerValue()
            observedValues.add(timerValue)

            tapTimer()
            Thread.sleep(20)
            tapTimer()
            Thread.sleep(20)
            tapTimer()
            Thread.sleep(50)
        }

        val maxObserved = observedValues.maxOrNull()!!
        assertTrue(
            "Rapid triple-taps should never cause timer to exceed phase duration. Duration: $goDuration, Max observed: $maxObserved, Sample values: ${observedValues.take(10)}",
            maxObserved <= goDuration
        )
    }

    @Test
    fun rapidResetButton_neverShowsTimerExceedingPhaseDuration() {
        composeTestRule.activityRule.scenario.close()

        val goDuration = 60
        val stopDuration = 15
        val config = TimerConfig(goDuration = goDuration, stopDuration = stopDuration)
        configRepository.saveConfig(config)
        stateRepository.clearState()

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val observedValues = mutableListOf<Int>()

        repeat(30) {
            val timerValue = getTimerValue()
            observedValues.add(timerValue)

            composeTestRule.onNodeWithTag("resetTimerButton").performClick()
            composeTestRule.waitForIdle()
            Thread.sleep(50)
        }

        val maxObserved = observedValues.maxOrNull()!!
        assertTrue(
            "Rapid reset button clicks should never cause timer to exceed phase duration. Duration: $goDuration, Max observed: $maxObserved, Sample values: ${observedValues.take(10)}",
            maxObserved <= goDuration
        )
    }

    @Test
    fun rapidResetFromSettings_neverShowsTimerExceedingPhaseDuration() {
        composeTestRule.activityRule.scenario.close()

        val goDuration = 60
        val stopDuration = 15
        val config = TimerConfig(goDuration = goDuration, stopDuration = stopDuration)
        configRepository.saveConfig(config)
        stateRepository.clearState()

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val observedValues = mutableListOf<Int>()

        repeat(15) {
            composeTestRule.onNodeWithTag("settingsButton").performClick()
            composeTestRule.waitForIdle()
            Thread.sleep(50)

            composeTestRule.onNodeWithTag("saveButton").performClick()
            composeTestRule.waitForIdle()
            Thread.sleep(50)

            val timerValue = getTimerValue()
            observedValues.add(timerValue)
        }

        val maxObserved = observedValues.maxOrNull()!!
        assertTrue(
            "Rapid settings save should never cause timer to exceed phase duration. Duration: $goDuration, Max observed: $maxObserved, Sample values: ${observedValues.take(10)}",
            maxObserved <= goDuration
        )
    }

    @Test
    fun phaseTransitionWithGrowth_neverShowsTimerExceedingCurrentPhaseDuration() {
        composeTestRule.activityRule.scenario.close()

        stateRepository.clearState()
        val goDuration = 2
        val stopDuration = 2
        val growthMultiplier = 1.2f
        val config = TimerConfig(
            goDuration = goDuration,
            stopDuration = stopDuration,
            goDurationGrowth = growthMultiplier,
            stopDurationGrowth = growthMultiplier
        )
        configRepository.saveConfig(config)

        manuallyLaunchedScenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.waitForIdle()

        val observedGoValues = mutableListOf<Int>()
        val observedStopValues = mutableListOf<Int>()

        tapTimer()
        composeTestRule.waitForIdle()

        repeat(3) { cycle ->
            Thread.sleep(2500)

            val goValue = getTimerValue()
            observedGoValues.add(goValue)
            val expectedMaxGo = (goDuration * growthMultiplier.toDouble().pow(cycle.toDouble())).toInt()

            assertTrue(
                "Go phase cycle $cycle should not exceed expected duration. Expected max: $expectedMaxGo, Observed: $goValue",
                goValue <= expectedMaxGo
            )

            Thread.sleep(2500)

            val stopValue = getTimerValue()
            observedStopValues.add(stopValue)
            val expectedMaxStop = (stopDuration * growthMultiplier.toDouble().pow(cycle.toDouble())).toInt()

            assertTrue(
                "Stop phase cycle $cycle should not exceed expected duration. Expected max: $expectedMaxStop, Observed: $stopValue",
                stopValue <= expectedMaxStop
            )
        }
    }
}
