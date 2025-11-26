package com.beavuck.stop_and_go.model

import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_COLOR
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GROWTH_MULTIPLIER
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_STOP_COLOR
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.model.TimerConstants.MAX_DURATION_SECONDS
import com.beavuck.stop_and_go.model.TimerConstants.MAX_GROWTH_MULTIPLIER
import com.beavuck.stop_and_go.model.TimerConstants.MIN_DURATION_SECONDS
import com.beavuck.stop_and_go.model.TimerConstants.MIN_GROWTH_MULTIPLIER

data class TimerConfig(
    val goDuration: Int = DEFAULT_GO_DURATION,
    val stopDuration: Int = DEFAULT_STOP_DURATION,
    val goDurationGrowth: Float = DEFAULT_GROWTH_MULTIPLIER,
    val stopDurationGrowth: Float = DEFAULT_GROWTH_MULTIPLIER,
    val goColor: String = DEFAULT_GO_COLOR,
    val stopColor: String = DEFAULT_STOP_COLOR
) {
    fun validate(): TimerConfig {
        require(goDuration in MIN_DURATION_SECONDS..MAX_DURATION_SECONDS) {
            "goDuration must be between $MIN_DURATION_SECONDS and $MAX_DURATION_SECONDS"
        }
        require(stopDuration in MIN_DURATION_SECONDS..MAX_DURATION_SECONDS) {
            "stopDuration must be between $MIN_DURATION_SECONDS and $MAX_DURATION_SECONDS"
        }
        require(goDurationGrowth in MIN_GROWTH_MULTIPLIER..MAX_GROWTH_MULTIPLIER) {
            "goDurationGrowth must be between $MIN_GROWTH_MULTIPLIER and $MAX_GROWTH_MULTIPLIER"
        }
        require(stopDurationGrowth in MIN_GROWTH_MULTIPLIER..MAX_GROWTH_MULTIPLIER) {
            "stopDurationGrowth must be between $MIN_GROWTH_MULTIPLIER and $MAX_GROWTH_MULTIPLIER"
        }
        return this
    }
}
