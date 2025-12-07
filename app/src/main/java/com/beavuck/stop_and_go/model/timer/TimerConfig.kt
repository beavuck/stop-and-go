package com.beavuck.stop_and_go.model.timer

data class TimerConfig(
    val goDuration: Int = TimerConstants.DEFAULT_GO_DURATION,
    val stopDuration: Int = TimerConstants.DEFAULT_STOP_DURATION,
    val goDurationGrowth: Float = TimerConstants.DEFAULT_GROWTH_MULTIPLIER,
    val stopDurationGrowth: Float = TimerConstants.DEFAULT_GROWTH_MULTIPLIER,
    val goColor: String = TimerConstants.DEFAULT_GO_COLOR,
    val stopColor: String = TimerConstants.DEFAULT_STOP_COLOR
) {
    fun validate(): TimerConfig {
        require(goDuration in TimerConstants.MIN_DURATION_SECONDS..TimerConstants.MAX_DURATION_SECONDS) {
            "goDuration must be between ${TimerConstants.MIN_DURATION_SECONDS} and ${TimerConstants.MAX_DURATION_SECONDS}"
        }
        require(stopDuration in TimerConstants.MIN_DURATION_SECONDS..TimerConstants.MAX_DURATION_SECONDS) {
            "stopDuration must be between ${TimerConstants.MIN_DURATION_SECONDS} and ${TimerConstants.MAX_DURATION_SECONDS}"
        }
        require(goDurationGrowth in TimerConstants.MIN_GROWTH_MULTIPLIER..TimerConstants.MAX_GROWTH_MULTIPLIER) {
            "goDurationGrowth must be between ${TimerConstants.MIN_GROWTH_MULTIPLIER} and ${TimerConstants.MAX_GROWTH_MULTIPLIER}"
        }
        require(stopDurationGrowth in TimerConstants.MIN_GROWTH_MULTIPLIER..TimerConstants.MAX_GROWTH_MULTIPLIER) {
            "stopDurationGrowth must be between ${TimerConstants.MIN_GROWTH_MULTIPLIER} and ${TimerConstants.MAX_GROWTH_MULTIPLIER}"
        }
        return this
    }
}