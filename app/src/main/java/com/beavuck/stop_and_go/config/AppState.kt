package com.beavuck.stop_and_go.config

import com.beavuck.stop_and_go.model.timer.TimerConstants

data class AppState(
    val cycleCount: Int = TimerConstants.INITIAL_CYCLE_COUNT,
    val isGo: Boolean = TimerConstants.DEFAULT_IS_GO,
    val currentGoDuration: Int = TimerConstants.DEFAULT_GO_DURATION,
    val currentStopDuration: Int = TimerConstants.DEFAULT_STOP_DURATION,
    val secondsRemaining: Int = TimerConstants.DEFAULT_GO_DURATION,
    val baseGoDuration: Int = TimerConstants.DEFAULT_GO_DURATION,
    val baseStopDuration: Int = TimerConstants.DEFAULT_STOP_DURATION,
    val isPaused: Boolean = TimerConstants.DEFAULT_IS_PAUSED
)
