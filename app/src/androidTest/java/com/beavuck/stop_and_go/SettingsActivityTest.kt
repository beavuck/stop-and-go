package com.beavuck.stop_and_go

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.activities.SettingsActivity
import com.beavuck.stop_and_go.model.TimerConfig
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_COLOR
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GROWTH_MULTIPLIER
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_STOP_COLOR
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.repositories.ConfigRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
    private lateinit var context: Context
    private lateinit var configRepository: ConfigRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        configRepository = ConfigRepository(context)
        configRepository.saveConfig(TimerConfig())
        configRepository.saveLocale("")
    }

    @After
    fun tearDown() {
        val sharedPreferences =
            context.getSharedPreferences("stop_and_go_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun settingsActivity_displaysAllInputFields() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.go_duration_input)).check(matches(isDisplayed()))
            onView(withId(R.id.stop_duration_input)).check(matches(isDisplayed()))
            onView(withId(R.id.go_growth_input)).check(matches(isDisplayed()))
            onView(withId(R.id.stop_growth_input)).check(matches(isDisplayed()))
            onView(withId(R.id.go_color_input)).check(matches(isDisplayed()))
            onView(withId(R.id.stop_color_input)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun settingsActivity_displaysAllButtons() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.save_button)).check(matches(isDisplayed()))
            onView(withId(R.id.reset_button)).check(matches(isDisplayed()))
            onView(withId(R.id.language_button)).check(matches(isDisplayed()))
            onView(withId(R.id.go_color_picker_button)).check(matches(isDisplayed()))
            onView(withId(R.id.stop_color_picker_button)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun settingsActivity_loadsDefaultConfig() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.go_duration_input)).check(matches(withText(DEFAULT_GO_DURATION.toString())))
            onView(withId(R.id.stop_duration_input)).check(matches(withText(DEFAULT_STOP_DURATION.toString())))
            onView(withId(R.id.go_growth_input)).check(matches(withText(DEFAULT_GROWTH_MULTIPLIER.toString())))
            onView(withId(R.id.stop_growth_input)).check(matches(withText(DEFAULT_GROWTH_MULTIPLIER.toString())))
            onView(withId(R.id.go_color_input)).check(matches(withText(DEFAULT_GO_COLOR)))
            onView(withId(R.id.stop_color_input)).check(matches(withText(DEFAULT_STOP_COLOR)))
        }
    }

    @Test
    fun settingsActivity_loadsSavedConfig() {
        val customConfig = TimerConfig(
            goDuration = 120,
            stopDuration = 30,
            goDurationGrowth = 1.5f,
            stopDurationGrowth = 0.8f,
            goColor = "#FF0000",
            stopColor = "#00FF00"
        )
        configRepository.saveConfig(customConfig)

        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.go_duration_input)).check(matches(withText("120")))
            onView(withId(R.id.stop_duration_input)).check(matches(withText("30")))
            onView(withId(R.id.go_growth_input)).check(matches(withText("1.5")))
            onView(withId(R.id.stop_growth_input)).check(matches(withText("0.8")))
            onView(withId(R.id.go_color_input)).check(matches(withText("#FF0000")))
            onView(withId(R.id.stop_color_input)).check(matches(withText("#00FF00")))
        }
    }

    @Test
    fun colorInput_canBeCleared_withoutCrashing() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.go_color_input)).perform(clearText())
            onView(withId(R.id.go_color_input)).check(matches(withText("")))

            onView(withId(R.id.stop_color_input)).perform(clearText())
            onView(withId(R.id.stop_color_input)).check(matches(withText("")))
        }
    }

    @Test
    fun colorInput_canBeModified() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.go_color_input)).perform(replaceText("#ABCDEF"))
            onView(withId(R.id.go_color_input)).check(matches(withText("#ABCDEF")))

            onView(withId(R.id.stop_color_input)).perform(replaceText("#123456"))
            onView(withId(R.id.stop_color_input)).check(matches(withText("#123456")))
        }
    }

    @Test
    fun saveButton_withValidInputs_savesConfig() {
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            onView(withId(R.id.go_duration_input)).perform(replaceText("90"))
            onView(withId(R.id.stop_duration_input)).perform(replaceText("20"))
            onView(withId(R.id.go_growth_input)).perform(replaceText("1.2"))
            onView(withId(R.id.stop_growth_input)).perform(replaceText("1.1"))
            onView(withId(R.id.go_color_input)).perform(replaceText("#AABBCC"))
            onView(withId(R.id.stop_color_input)).perform(replaceText("#DDEEFF"))

            onView(withId(R.id.save_button)).perform(click())

            scenario.onActivity {
                val savedConfig = configRepository.loadConfig()
                assertEquals(90, savedConfig.goDuration)
                assertEquals(20, savedConfig.stopDuration)
                assertEquals(1.2f, savedConfig.goDurationGrowth, 0.001f)
                assertEquals(1.1f, savedConfig.stopDurationGrowth, 0.001f)
                assertEquals("#AABBCC", savedConfig.goColor)
                assertEquals("#DDEEFF", savedConfig.stopColor)
            }
        }
    }

    @Test
    fun durationInputs_canBeModified() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.go_duration_input)).perform(replaceText("100"))
            onView(withId(R.id.go_duration_input)).check(matches(withText("100")))

            onView(withId(R.id.stop_duration_input)).perform(replaceText("25"))
            onView(withId(R.id.stop_duration_input)).check(matches(withText("25")))
        }
    }

    @Test
    fun growthInputs_canBeModified() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.go_growth_input)).perform(replaceText("2.0"))
            onView(withId(R.id.go_growth_input)).check(matches(withText("2.0")))

            onView(withId(R.id.stop_growth_input)).perform(replaceText("0.5"))
            onView(withId(R.id.stop_growth_input)).check(matches(withText("0.5")))
        }
    }

    @Test
    fun saveButton_withEmptyFields_usesDefaultValues() {
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            onView(withId(R.id.go_duration_input)).perform(clearText())
            onView(withId(R.id.stop_duration_input)).perform(clearText())
            onView(withId(R.id.go_growth_input)).perform(clearText())
            onView(withId(R.id.stop_growth_input)).perform(clearText())
            onView(withId(R.id.go_color_input)).perform(clearText())
            onView(withId(R.id.stop_color_input)).perform(clearText())

            onView(withId(R.id.save_button)).perform(click())

            scenario.onActivity {
                val savedConfig = configRepository.loadConfig()
                assertEquals(DEFAULT_GO_DURATION, savedConfig.goDuration)
                assertEquals(DEFAULT_STOP_DURATION, savedConfig.stopDuration)
                assertEquals(DEFAULT_GROWTH_MULTIPLIER, savedConfig.goDurationGrowth, 0.001f)
                assertEquals(DEFAULT_GROWTH_MULTIPLIER, savedConfig.stopDurationGrowth, 0.001f)
                assertEquals(DEFAULT_GO_COLOR, savedConfig.goColor)
                assertEquals(DEFAULT_STOP_COLOR, savedConfig.stopColor)
            }
        }
    }

    @Test
    fun saveButton_withPartiallyEmptyFields_usesDefaultsForEmptyFields() {
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            onView(withId(R.id.go_duration_input)).perform(replaceText("100"))
            onView(withId(R.id.stop_duration_input)).perform(clearText())
            onView(withId(R.id.go_growth_input)).perform(clearText())
            onView(withId(R.id.stop_growth_input)).perform(replaceText("1.5"))
            onView(withId(R.id.go_color_input)).perform(replaceText("#FF0000"))
            onView(withId(R.id.stop_color_input)).perform(clearText())

            onView(withId(R.id.save_button)).perform(click())

            scenario.onActivity {
                val savedConfig = configRepository.loadConfig()
                assertEquals(100, savedConfig.goDuration)
                assertEquals(DEFAULT_STOP_DURATION, savedConfig.stopDuration)
                assertEquals(DEFAULT_GROWTH_MULTIPLIER, savedConfig.goDurationGrowth, 0.001f)
                assertEquals(1.5f, savedConfig.stopDurationGrowth, 0.001f)
                assertEquals("#FF0000", savedConfig.goColor)
                assertEquals(DEFAULT_STOP_COLOR, savedConfig.stopColor)
            }
        }
    }
}
