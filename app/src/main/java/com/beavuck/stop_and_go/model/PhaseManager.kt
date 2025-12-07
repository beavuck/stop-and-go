package com.beavuck.stop_and_go.model

import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.model.timer.TimerConstants.INITIAL_CYCLE_COUNT
import com.beavuck.stop_and_go.model.timer.TimerConstants.MAX_DURATION_SECONDS
import com.beavuck.stop_and_go.model.timer.TimerConstants.MIN_DURATION_SECONDS
import kotlin.math.roundToInt

class PhaseManager(private val config: TimerConfig) {
    private var currentGoDuration: Int = config.goDuration
    private var currentStopDuration: Int = config.stopDuration
    private var isCurrentlyGo: Boolean = true
    private var _cycleCount: Int = INITIAL_CYCLE_COUNT
    val cycleCount: Int
        get() = _cycleCount


    fun getCurrentPhase(): PhaseState {
        return if (isCurrentlyGo) {
            PhaseState(true, config.goColor, currentGoDuration)
        } else {
            PhaseState(false, config.stopColor, currentStopDuration)
        }
    }

    fun advanceToNextPhase() {
        if (isCurrentlyGo) {
            isCurrentlyGo = false
        } else {
            isCurrentlyGo = true
            _cycleCount++
            applyGrowthToGoAndStop()
        }
    }

    private fun applyGrowthToGoAndStop() {
        currentGoDuration = applyGrowth(currentGoDuration, config.goDurationGrowth)
        currentStopDuration = applyGrowth(currentStopDuration, config.stopDurationGrowth)
    }

    private fun applyGrowth(duration: Int, growth: Float): Int = (duration * growth)
        .roundToInt()
        .coerceIn(MIN_DURATION_SECONDS, MAX_DURATION_SECONDS)

    fun reset() {
        currentGoDuration = config.goDuration
        currentStopDuration = config.stopDuration
        _cycleCount = INITIAL_CYCLE_COUNT
        isCurrentlyGo = true
    }

    fun isGo(): Boolean = isCurrentlyGo

    fun getGoLabel(): String = config.goLabel

    fun getStopLabel(): String = config.stopLabel

    fun getState(secondsRemaining: Int): AppState {
        return AppState(
            cycleCount = _cycleCount,
            isGo = isCurrentlyGo,
            currentGoDuration = currentGoDuration,
            currentStopDuration = currentStopDuration,
            secondsRemaining = secondsRemaining,
            baseGoDuration = config.goDuration,
            baseStopDuration = config.stopDuration
        )
    }

    fun getState(): AppState {
        return AppState(
            cycleCount = _cycleCount,
            isGo = isCurrentlyGo,
            currentGoDuration = currentGoDuration,
            currentStopDuration = currentStopDuration,
            secondsRemaining = if (isCurrentlyGo) currentGoDuration else currentStopDuration,
            baseGoDuration = config.goDuration,
            baseStopDuration = config.stopDuration
        )
    }

    fun restoreState(state: AppState) {
        _cycleCount = state.cycleCount
        isCurrentlyGo = state.isGo
        currentGoDuration = state.currentGoDuration
        currentStopDuration = state.currentStopDuration
    }
}
