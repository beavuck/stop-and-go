package com.beavuck.stop_and_go.model

data class AppState(
    val cycleCount: Int,
    val isGo: Boolean,
    val currentGoDuration: Int,
    val currentStopDuration: Int,
    val secondsRemaining: Int,
    val baseGoDuration: Int = 0,
    val baseStopDuration: Int = 0
)
