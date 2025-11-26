package com.beavuck.stop_and_go.model

object TimerConstants {
    const val MIN_DURATION_SECONDS = 1
    const val MAX_DURATION_SECONDS = 3600
    const val MIN_GROWTH_MULTIPLIER = 0.01f
    const val MAX_GROWTH_MULTIPLIER = 100.0f

    const val DEFAULT_GO_DURATION = 60
    const val DEFAULT_STOP_DURATION = 15
    const val DEFAULT_GROWTH_MULTIPLIER = 1.0f
    const val DEFAULT_GO_COLOR = "#20b05c"
    const val DEFAULT_STOP_COLOR = "#992639"
    const val DEFAULT_COLOR = "#000000"

    const val INITIAL_CYCLE_COUNT = 0

    const val MILLIS_PER_SECOND = 1000L
    const val TIMER_DISPLAY_OFFSET = 1

    const val HEX_COLOR_FORMAT = "#%06X"
    const val COLOR_MASK = 0xFFFFFF

    val GO_VIBRATION_PATTERN = longArrayOf(0, 200, 100, 200)
    val STOP_VIBRATION_PATTERN = longArrayOf(0, 768)
}
