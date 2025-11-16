package com.beavuck.stop_and_go

import android.graphics.Color
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_COLOR
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.TimerConstants.INITIAL_CYCLE_COUNT
import com.beavuck.stop_and_go.activities.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
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
    fun initialBackgroundColor_isGoColor() {
        scenario.onActivity { activity ->
            val expectedColor = Color.parseColor(DEFAULT_GO_COLOR)
            val layout = activity.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(
                R.id.main
            )
            val actualColor = layout.background?.let {
                (it as? android.graphics.drawable.ColorDrawable)?.color
            }
            assert(actualColor == expectedColor)
        }
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

        Thread.sleep(100)

        scenario.onActivity { activity ->
            val timerValueAfterRecreation = activity.findViewById<TextView>(
                R.id.timerText
            ).text.toString()
            assert(timerValueBeforeRecreation.toInt() != timerValueAfterRecreation.toInt())
        }
    }
}
