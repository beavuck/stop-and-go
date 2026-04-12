package com.beavuck.stop_and_go.model.timer

import android.content.Context
import com.beavuck.stop_and_go.R
import kotlinx.serialization.Serializable

@Serializable
data class TimerConfig(
    val goDuration: Int = TimerConstants.DEFAULT_GO_DURATION,
    val stopDuration: Int = TimerConstants.DEFAULT_STOP_DURATION,
    val goDurationGrowth: Float = TimerConstants.DEFAULT_GROWTH_MULTIPLIER,
    val stopDurationGrowth: Float = TimerConstants.DEFAULT_GROWTH_MULTIPLIER,
    val goColor: String = TimerConstants.DEFAULT_GO_COLOR,
    val stopColor: String = TimerConstants.DEFAULT_STOP_COLOR,
    val goLabel: String = "",
    val stopLabel: String = "",
    val soundEnabled: Boolean = TimerConstants.DEFAULT_SOUND_ENABLED
) {
    fun validate(context: Context? = null): TimerConfig {
        require(goDuration in TimerConstants.MIN_DURATION_SECONDS..TimerConstants.MAX_DURATION_SECONDS) {
            context?.getString(
                R.string.error_go_duration_range,
                TimerConstants.MIN_DURATION_SECONDS,
                TimerConstants.MAX_DURATION_SECONDS
            )
                ?: "Go duration must be between ${TimerConstants.MIN_DURATION_SECONDS} and ${TimerConstants.MAX_DURATION_SECONDS} seconds"
        }
        require(stopDuration in TimerConstants.MIN_DURATION_SECONDS..TimerConstants.MAX_DURATION_SECONDS) {
            context?.getString(
                R.string.error_stop_duration_range,
                TimerConstants.MIN_DURATION_SECONDS,
                TimerConstants.MAX_DURATION_SECONDS
            )
                ?: "Stop duration must be between ${TimerConstants.MIN_DURATION_SECONDS} and ${TimerConstants.MAX_DURATION_SECONDS} seconds"
        }
        require(goDurationGrowth in TimerConstants.MIN_GROWTH_MULTIPLIER..TimerConstants.MAX_GROWTH_MULTIPLIER) {
            context?.getString(
                R.string.error_go_growth_range,
                TimerConstants.MIN_GROWTH_MULTIPLIER,
                TimerConstants.MAX_GROWTH_MULTIPLIER
            )
                ?: "Go growth multiplier must be between ${TimerConstants.MIN_GROWTH_MULTIPLIER} and ${TimerConstants.MAX_GROWTH_MULTIPLIER}"
        }
        require(stopDurationGrowth in TimerConstants.MIN_GROWTH_MULTIPLIER..TimerConstants.MAX_GROWTH_MULTIPLIER) {
            context?.getString(
                R.string.error_stop_growth_range,
                TimerConstants.MIN_GROWTH_MULTIPLIER,
                TimerConstants.MAX_GROWTH_MULTIPLIER
            )
                ?: "Stop growth multiplier must be between ${TimerConstants.MIN_GROWTH_MULTIPLIER} and ${TimerConstants.MAX_GROWTH_MULTIPLIER}"
        }
        require(goLabel.length <= TimerConstants.BASE_MAX_STRING_INPUT_LENGTH) {
            context?.getString(
                R.string.error_go_label_length,
                TimerConstants.BASE_MAX_STRING_INPUT_LENGTH
            )
                ?: "Go label must not exceed ${TimerConstants.BASE_MAX_STRING_INPUT_LENGTH} characters"
        }
        require(stopLabel.length <= TimerConstants.BASE_MAX_STRING_INPUT_LENGTH) {
            context?.getString(
                R.string.error_stop_label_length,
                TimerConstants.BASE_MAX_STRING_INPUT_LENGTH
            )
                ?: "Stop label must not exceed ${TimerConstants.BASE_MAX_STRING_INPUT_LENGTH} characters"
        }
        return this
    }
}