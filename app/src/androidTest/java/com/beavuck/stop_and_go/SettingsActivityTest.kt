package com.beavuck.stop_and_go

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.activities.SettingsActivity
import com.beavuck.stop_and_go.config.AppState
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.model.timer.TimerConstants
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SettingsActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var stateRepository: StateRepository
    private lateinit var configRepository: ConfigRepository

    @Before
    fun setup() {
        stateRepository = StateRepository(context)
        configRepository = ConfigRepository(context)
        stateRepository.clearState()
        configRepository.saveConfig(TimerConfig())
        configRepository.saveLocale(DEFAULT_LOCALE.code)
    }

    @After
    fun tearDown() {
        stateRepository.clearState()
        configRepository.saveConfig(TimerConfig())
        configRepository.saveLocale(DEFAULT_LOCALE.code)
    }

    @Test
    fun settingsScreen_displaysActionButtons() {
        composeTestRule.onNodeWithTag("saveButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("resetButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("languageButton").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAllSettingInputs() {
        composeTestRule.onNodeWithTag("goDurationInput").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("goGrowthInput").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("stopDurationInput").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("stopGrowthInput").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("goColorInput").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("stopColorInput").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("goLabelInput").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("stopLabelInput").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun settingsScreen_colorButtons_openColorPicker() {
        composeTestRule.onNodeWithTag("goColorButton").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("colorPreview").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun settingsScreen_languageButton_opensLanguagePicker() {
        composeTestRule.onNodeWithTag("languageButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("locale_en").assertIsDisplayed()
        composeTestRule.onNodeWithTag("languageList")
            .performScrollToNode(hasTestTag("locale_ar"))
        composeTestRule.onNodeWithTag("locale_ar").assertIsDisplayed()
    }

    @Test
    fun saveButton_withValidInput_savesConfigAndClearsState() {
        composeTestRule.onNodeWithTag("goDurationInput").performScrollTo().performTextClearance()
        composeTestRule.onNodeWithTag("goDurationInput").performScrollTo().performTextInput("120")

        composeTestRule.onNodeWithTag("saveButton").performClick()
        composeTestRule.waitForIdle()

        val savedConfig = configRepository.loadConfig()
        assertEquals(120, savedConfig.goDuration)

        val savedState = stateRepository.loadState()
        assertEquals(null, savedState)
    }

    @Test
    fun resetButton_clearsStateAndFinishes() {
        val testState = AppState(
            cycleCount = 5,
            isGo = false,
            currentGoDuration = 100,
            currentStopDuration = 50,
            secondsRemaining = 25
        )
        stateRepository.saveState(testState)

        composeTestRule.onNodeWithTag("resetButton").performClick()
        composeTestRule.waitForIdle()

        val clearedState = stateRepository.loadState()
        assertEquals(null, clearedState)
    }

    @Test
    fun settingsScreen_loadsDefaultValues() {
        configRepository.saveConfig(TimerConfig())
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("goDurationInput").performScrollTo()
            .assertTextContains(TimerConstants.DEFAULT_GO_DURATION.toString())
        composeTestRule.onNodeWithTag("stopDurationInput").performScrollTo()
            .assertTextContains(TimerConstants.DEFAULT_STOP_DURATION.toString())
    }

    @Test
    fun colorPicker_selectColor_updatesInput() {
        composeTestRule.onNodeWithTag("goColorButton").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("confirmButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("goColorInput").performScrollTo().assertExists()
    }

    @Test
    fun settingsScreen_inputsAcceptUserInput() {
        composeTestRule.onNodeWithTag("goDurationInput").performScrollTo().performTextClearance()
        composeTestRule.onNodeWithTag("goDurationInput").performScrollTo().performTextInput("300")

        composeTestRule.onNodeWithTag("goDurationInput").performScrollTo()
            .assertTextContains("300")
    }

    @Test
    fun saveButton_withInvalidColorHex_showsErrorAndDoesNotSave() {
        composeTestRule.onNodeWithTag("goColorInput").performScrollTo().performTextClearance()
        composeTestRule.onNodeWithTag("goColorInput").performScrollTo().performTextInput("#pppppp")

        composeTestRule.onNodeWithTag("saveButton").performClick()
        composeTestRule.waitForIdle()

        val savedConfig = configRepository.loadConfig()
        assertEquals(TimerConstants.DEFAULT_GO_COLOR, savedConfig.goColor)
    }

    @Test
    fun languageSelection_savesLocaleAndFinishes() {
        composeTestRule.onNodeWithTag("languageButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("languageList")
            .performScrollToNode(hasTestTag("locale_fr"))
        composeTestRule.onNodeWithTag("locale_fr").performClick()

        val savedLocale = configRepository.loadLocale()
        assertEquals(SupportedLocale.FRENCH.code, savedLocale)
    }

    @Test
    fun saveButton_withCustomLabels_savesLabelsToConfig() {
        composeTestRule.onNodeWithTag("goLabelInput").performScrollTo().performTextInput("Sprint")
        composeTestRule.onNodeWithTag("stopLabelInput").performScrollTo().performTextInput("Rest")

        composeTestRule.onNodeWithTag("saveButton").performClick()
        composeTestRule.waitForIdle()

        val savedConfig = configRepository.loadConfig()
        assertEquals("Sprint", savedConfig.goLabel)
        assertEquals("Rest", savedConfig.stopLabel)
    }

    @Test
    fun settingsScreen_displaysSoundToggle() {
        composeTestRule.onNodeWithTag("soundEnabledToggle")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_soundToggle_savesToConfig() {
        val initialValue = configRepository.loadConfig().soundEnabled

        composeTestRule.onNodeWithTag("soundEnabledToggle")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithTag("saveButton").performClick()
        composeTestRule.waitForIdle()

        val savedConfig = configRepository.loadConfig()
        assertEquals(!initialValue, savedConfig.soundEnabled)
    }
}
