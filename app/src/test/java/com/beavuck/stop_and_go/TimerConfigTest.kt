package com.beavuck.stop_and_go

import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_COLOR
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GROWTH_MULTIPLIER
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_STOP_COLOR
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.MAX_DURATION_SECONDS
import com.beavuck.stop_and_go.model.timer.TimerConstants.MAX_GROWTH_MULTIPLIER
import com.beavuck.stop_and_go.model.timer.TimerConstants.MIN_DURATION_SECONDS
import com.beavuck.stop_and_go.model.timer.TimerConstants.MIN_GROWTH_MULTIPLIER
import org.junit.Assert.assertEquals
import org.junit.Test

class TimerConfigTest {
    @Test
    fun defaultConfig_hasCorrectValues() {
        val config = TimerConfig()

        assertEquals(DEFAULT_GO_DURATION, config.goDuration)
        assertEquals(DEFAULT_STOP_DURATION, config.stopDuration)
        assertEquals(DEFAULT_GROWTH_MULTIPLIER, config.goDurationGrowth)
        assertEquals(DEFAULT_GROWTH_MULTIPLIER, config.stopDurationGrowth)
        assertEquals(DEFAULT_GO_COLOR, config.goColor)
        assertEquals(DEFAULT_STOP_COLOR, config.stopColor)
    }

    @Test
    fun validate_withValidConfig_succeeds() {
        val config = TimerConfig(
            goDuration = 30,
            stopDuration = 10,
            goDurationGrowth = 1.1f,
            stopDurationGrowth = 1.05f
        )

        val validated = config.validate()

        assertEquals(config, validated)
    }

    @Test
    fun validate_withMinimumValues_succeeds() {
        val config = TimerConfig(
            goDuration = MIN_DURATION_SECONDS,
            stopDuration = MIN_DURATION_SECONDS,
            goDurationGrowth = MIN_GROWTH_MULTIPLIER,
            stopDurationGrowth = MIN_GROWTH_MULTIPLIER
        )

        val validated = config.validate()

        assertEquals(config, validated)
    }

    @Test
    fun validate_withMaximumValues_succeeds() {
        val config = TimerConfig(
            goDuration = MAX_DURATION_SECONDS,
            stopDuration = MAX_DURATION_SECONDS,
            goDurationGrowth = MAX_GROWTH_MULTIPLIER,
            stopDurationGrowth = MAX_GROWTH_MULTIPLIER
        )

        val validated = config.validate()

        assertEquals(config, validated)
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withGoDurationTooLow_throws() {
        val config = TimerConfig(goDuration = MIN_DURATION_SECONDS - 1)

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withGoDurationTooHigh_throws() {
        val config = TimerConfig(goDuration = MAX_DURATION_SECONDS + 1)

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withStopDurationTooLow_throws() {
        val config = TimerConfig(stopDuration = MIN_DURATION_SECONDS - 1)

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withStopDurationTooHigh_throws() {
        val config = TimerConfig(stopDuration = MAX_DURATION_SECONDS + 1)

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withGoDurationGrowthTooLow_throws() {
        val config = TimerConfig(goDurationGrowth = MIN_GROWTH_MULTIPLIER - 0.01f)

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withGoDurationGrowthTooHigh_throws() {
        val config = TimerConfig(goDurationGrowth = MAX_GROWTH_MULTIPLIER + 1f)

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withStopDurationGrowthTooLow_throws() {
        val config = TimerConfig(stopDurationGrowth = MIN_GROWTH_MULTIPLIER - 0.01f)

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withStopDurationGrowthTooHigh_throws() {
        val config = TimerConfig(stopDurationGrowth = MAX_GROWTH_MULTIPLIER + 1f)

        config.validate()
    }

    @Test
    fun defaultConfig_hasEmptyLabels() {
        val config = TimerConfig()

        assertEquals("", config.goLabel)
        assertEquals("", config.stopLabel)
    }

    @Test
    fun config_withCustomLabels_succeeds() {
        val config = TimerConfig(
            goLabel = "Run",
            stopLabel = "Walk"
        )

        assertEquals("Run", config.goLabel)
        assertEquals("Walk", config.stopLabel)
    }

    @Test
    fun validate_withValidLabels_succeeds() {
        val config = TimerConfig(
            goLabel = "Sprint",
            stopLabel = "Rest"
        )

        val validated = config.validate()

        assertEquals(config, validated)
    }

    @Test
    fun validate_withEmptyLabels_succeeds() {
        val config = TimerConfig(
            goLabel = "",
            stopLabel = ""
        )

        val validated = config.validate()

        assertEquals(config, validated)
    }

    @Test
    fun validate_withMaxLengthLabels_succeeds() {
        val maxLabel = "a".repeat(32)
        val config = TimerConfig(
            goLabel = maxLabel,
            stopLabel = maxLabel
        )

        val validated = config.validate()

        assertEquals(config, validated)
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withGoLabelTooLong_throws() {
        val config = TimerConfig(goLabel = "a".repeat(33))

        config.validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun validate_withStopLabelTooLong_throws() {
        val config = TimerConfig(stopLabel = "a".repeat(33))

        config.validate()
    }
}
