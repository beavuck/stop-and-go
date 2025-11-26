package com.beavuck.stop_and_go.model

import com.beavuck.stop_and_go.model.TimerConstants.INITIAL_CYCLE_COUNT
import com.beavuck.stop_and_go.model.TimerConstants.MAX_DURATION_SECONDS
import com.beavuck.stop_and_go.model.TimerConstants.MIN_DURATION_SECONDS

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
        .toInt()
        .coerceIn(MIN_DURATION_SECONDS, MAX_DURATION_SECONDS)

    fun reset() {
        currentGoDuration = config.goDuration
        currentStopDuration = config.stopDuration
        _cycleCount = INITIAL_CYCLE_COUNT
        isCurrentlyGo = true
    }

    fun isGo(): Boolean = isCurrentlyGo

    fun getState(): AppState {
        return AppState(
            cycleCount = _cycleCount,
            isGo = isCurrentlyGo,
            currentGoDuration = currentGoDuration,
            currentStopDuration = currentStopDuration,
            secondsRemaining = 0,
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
