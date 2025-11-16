package com.beavuck.stop_and_go.model

class PhaseManager(private val config: TimerConfig) {
    private var currentGoDuration: Int = config.goDuration
    private var currentStopDuration: Int = config.stopDuration
    private var isCurrentlyGo: Boolean = true
    private var _cycleCount: Int = TimerConstants.INITIAL_CYCLE_COUNT
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
        .coerceIn(TimerConstants.MIN_DURATION_SECONDS, TimerConstants.MAX_DURATION_SECONDS)

    fun reset() {
        currentGoDuration = config.goDuration
        currentStopDuration = config.stopDuration
        _cycleCount = TimerConstants.INITIAL_CYCLE_COUNT
        isCurrentlyGo = true
    }

    fun isGo(): Boolean = isCurrentlyGo
}
