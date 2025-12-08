package com.beavuck.stop_and_go.model

import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_IS_GO
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_IS_PAUSED
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.INITIAL_CYCLE_COUNT

data class AppState(
    val cycleCount: Int = INITIAL_CYCLE_COUNT,
    val isGo: Boolean = DEFAULT_IS_GO,
    val currentGoDuration: Int = DEFAULT_GO_DURATION,
    val currentStopDuration: Int = DEFAULT_STOP_DURATION,
    val secondsRemaining: Int = DEFAULT_GO_DURATION,
    val baseGoDuration: Int = DEFAULT_GO_DURATION,
    val baseStopDuration: Int = DEFAULT_STOP_DURATION,
    val isPaused: Boolean = DEFAULT_IS_PAUSED
)
