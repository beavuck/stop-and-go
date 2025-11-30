package com.beavuck.stop_and_go

import android.graphics.Color
import android.view.WindowManager
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.activities.MainActivity
import com.beavuck.stop_and_go.model.AppState
import com.beavuck.stop_and_go.model.TimerConfig
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.model.TimerConstants.INITIAL_CYCLE_COUNT
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.utils.ColorUtils
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        val stateRepository = StateRepository(ApplicationProvider.getApplicationContext())
        stateRepository.clearState()

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun activityLaunches_successfully() {
        scenario.onActivity { activity ->
            assert(activity != null)
        }
    }

    @Test
    fun timerText_isDisplayed() {
        onView(withId(R.id.timerText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun cycleCountText_isDisplayed() {
        onView(withId(R.id.cycleCount))
            .check(matches(isDisplayed()))
    }

    @Test
    fun phaseLabel_isDisplayed() {
        onView(withId(R.id.phaseLabel))
            .check(matches(isDisplayed()))
    }

    @Test
    fun initialPhaseLabel_showsGo() {
        var expectedLabel = ""
        scenario.onActivity { activity ->
            expectedLabel = activity.getString(R.string.phase_go)
        }

        onView(withId(R.id.phaseLabel))
            .check(matches(withText(expectedLabel)))
    }

    @Test
    fun initialTimerText_showsGoDuration() {
        onView(withId(R.id.timerText))
            .check(matches(withText(DEFAULT_GO_DURATION.toString())))
    }

    @Test
    fun initialCycleCount_showsOne() {
        var expectedText = ""
        scenario.onActivity { activity ->
            expectedText = activity.getString(R.string.cycle_count, INITIAL_CYCLE_COUNT + 1)
        }

        onView(withId(R.id.cycleCount))
            .check(matches(withText(expectedText)))
    }


    @Test
    fun timerText_changesAfterOneSecond() {
        Thread.sleep(1500)

        onView(withId(R.id.timerText))
            .check(matches(not(withText(DEFAULT_GO_DURATION.toString()))))
    }

    @Test
    fun timerText_countdownsCorrectly() {
        Thread.sleep(1500)

        onView(withId(R.id.timerText))
            .check(matches(withText((DEFAULT_GO_DURATION - 1).toString())))
    }

    @Test
    fun activityRecreation_maintainsTimerState() {
        Thread.sleep(1500)

        var timerValueBeforeRecreation = ""
        scenario.onActivity { activity ->
            timerValueBeforeRecreation = activity.findViewById<TextView>(
                R.id.timerText
            ).text.toString()
        }

        scenario.recreate()

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val timerValueAfterRecreation = activity.findViewById<TextView>(
                R.id.timerText
            ).text.toString()
            val beforeValue = timerValueBeforeRecreation.toIntOrNull() ?: 0
            val afterValue = timerValueAfterRecreation.toIntOrNull() ?: 0
            assert(afterValue in (beforeValue - 2)..(beforeValue + 1))
        }
    }

    @Test
    fun activityLaunch_restoresStopPhase() {
        scenario.close()

        val stateRepository = StateRepository(ApplicationProvider.getApplicationContext())
        val stopPhaseState = AppState(
            cycleCount = 1,
            isGo = false,
            currentGoDuration = DEFAULT_GO_DURATION,
            currentStopDuration = DEFAULT_STOP_DURATION,
            secondsRemaining = 10,
            baseGoDuration = DEFAULT_GO_DURATION,
            baseStopDuration = DEFAULT_STOP_DURATION
        )
        stateRepository.saveState(stopPhaseState)

        scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(500)

        var expectedLabel = ""
        scenario.onActivity { activity ->
            expectedLabel = activity.getString(R.string.phase_stop)
        }

        onView(withId(R.id.phaseLabel))
            .check(matches(withText(expectedLabel)))

        scenario.onActivity { activity ->
            val timerValue = activity.findViewById<TextView>(R.id.timerText).text.toString()
            val value = timerValue.toIntOrNull() ?: 0
            assert(value in 8..10)
        }
    }

    @Test
    fun timerRunning_keepsScreenOn() {
        scenario.onActivity { activity ->
            val flags = activity.window.attributes.flags
            val keepScreenOn = flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            assert(keepScreenOn != 0) { "FLAG_KEEP_SCREEN_ON should be set when timer is running" }
        }
    }

    @Test
    fun localeChange_recreatesActivity() {
        val configRepository = ConfigRepository(ApplicationProvider.getApplicationContext())

        var activityHashBeforeChange = 0
        scenario.onActivity { activity ->
            activityHashBeforeChange = System.identityHashCode(activity)
        }

        configRepository.saveLocale("fr")

        scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.onResume()
            }
        }

        Thread.sleep(500)

        scenario.onActivity { activity ->
            val activityHashAfterChange = System.identityHashCode(activity)
            assertNotEquals(activityHashBeforeChange, activityHashAfterChange)
        }
    }

    @Test
    fun localeChange_preservesTimerState() {
        val configRepository = ConfigRepository(ApplicationProvider.getApplicationContext())

        Thread.sleep(1500)

        var timerValueBeforeChange = ""
        scenario.onActivity { activity ->
            timerValueBeforeChange = activity.findViewById<TextView>(R.id.timerText).text.toString()
        }

        configRepository.saveLocale("es")

        scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.onResume()
            }
        }

        Thread.sleep(1000)

        scenario.onActivity { activity ->
            val timerValueAfterChange =
                activity.findViewById<TextView>(R.id.timerText).text.toString()
            val beforeValue = timerValueBeforeChange.toIntOrNull() ?: 0
            val afterValue = timerValueAfterChange.toIntOrNull() ?: 0
            assert(afterValue in (beforeValue - 3)..(beforeValue + 1))
        }
    }

    @Test
    fun textColor_contrastsWithLightBackground() {
        scenario.close()

        val lightConfig = TimerConfig(goColor = "#FFFFFF", stopColor = "#F0F0F0")
        val configRepository = ConfigRepository(ApplicationProvider.getApplicationContext())
        configRepository.saveConfig(lightConfig)

        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            val backgroundColor = Color.parseColor("#FFFFFF")
            val expectedTextColor = ColorUtils.getContrastingTextColor(backgroundColor)

            val timerText = activity.findViewById<TextView>(R.id.timerText)
            val phaseLabelText = activity.findViewById<TextView>(R.id.phaseLabel)
            val cycleCountText = activity.findViewById<TextView>(R.id.cycleCount)

            assertEquals(expectedTextColor, timerText.currentTextColor)
            assertEquals(expectedTextColor, phaseLabelText.currentTextColor)
            assertEquals(expectedTextColor, cycleCountText.currentTextColor)

            assert(Color.luminance(expectedTextColor) < 0.5)
        }
    }

    @Test
    fun textColor_contrastsWithDarkBackground() {
        scenario.close()

        val darkConfig = TimerConfig(goColor = "#000000", stopColor = "#202020")
        val configRepository = ConfigRepository(ApplicationProvider.getApplicationContext())
        configRepository.saveConfig(darkConfig)

        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            val backgroundColor = Color.parseColor("#000000")
            val expectedTextColor = ColorUtils.getContrastingTextColor(backgroundColor)

            val timerText = activity.findViewById<TextView>(R.id.timerText)
            val phaseLabelText = activity.findViewById<TextView>(R.id.phaseLabel)
            val cycleCountText = activity.findViewById<TextView>(R.id.cycleCount)

            assertEquals(expectedTextColor, timerText.currentTextColor)
            assertEquals(expectedTextColor, phaseLabelText.currentTextColor)
            assertEquals(expectedTextColor, cycleCountText.currentTextColor)

            assert(Color.luminance(expectedTextColor) > 0.5)
        }
    }
}
