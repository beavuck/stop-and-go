package com.beavuck.stop_and_go.model.timer

object TimerConstants {
    const val MIN_DURATION_SECONDS = 1
    const val MAX_DURATION_SECONDS = 3600
    const val MIN_GROWTH_MULTIPLIER = 0.01f
    const val MAX_GROWTH_MULTIPLIER = 100.0f

    const val DEFAULT_IS_GO = true
    const val DEFAULT_GO_DURATION = 30
    const val DEFAULT_STOP_DURATION = 10
    const val DEFAULT_GROWTH_MULTIPLIER = 1.0f
    const val DEFAULT_GO_COLOR = "#5B8394"
    const val DEFAULT_STOP_COLOR = "#8B7366"
    const val DEFAULT_SOUND_ENABLED = true
    const val DEFAULT_IS_PAUSED = true

    const val DEBOUNCE_DELAY = 500L

    const val BASE_MAX_STRING_INPUT_LENGTH = 32

    const val INITIAL_CYCLE_COUNT = 0
}
