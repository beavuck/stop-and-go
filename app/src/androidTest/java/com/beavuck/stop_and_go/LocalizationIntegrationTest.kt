package com.beavuck.stop_and_go

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.activities.MainActivity
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class LocalizationIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var stateRepository: StateRepository
    private lateinit var configRepository: ConfigRepository
    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        stateRepository = StateRepository(context)
        configRepository = ConfigRepository(context)
        val tutorialRepository = TutorialRepository(context)

        stateRepository.clearState()
        tutorialRepository.markTutorialCompleteSync()
        composeTestRule.activityRule.scenario.close()
    }

    @After
    fun tearDown() {
        scenario?.close()
        stateRepository.clearState()
        configRepository.saveLocale(DEFAULT_LOCALE.code)
    }

    @Test
    fun selectingLocale_showsCorrectLocalizedLabel() {
        SupportedLocale.entries.forEach { locale ->
            val localeConfig = android.content.res.Configuration(context.resources.configuration)
            localeConfig.setLocale(Locale.forLanguageTag(locale.code))
            val localeContext = context.createConfigurationContext(localeConfig)
            val expectedLabel = localeContext.getString(R.string.phase_go)

            configRepository.saveLocale(locale.code)
            scenario = ActivityScenario.launch(MainActivity::class.java)
            composeTestRule.waitForIdle()

            val label = composeTestRule.onNodeWithTag("phaseLabel")
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .first().text

            assertEquals(
                "Locale ${locale.code} should show '$expectedLabel' but showed '$label'",
                expectedLabel,
                label
            )

            scenario?.close()
            scenario = null
        }
    }
}