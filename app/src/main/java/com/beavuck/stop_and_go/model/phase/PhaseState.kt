package com.beavuck.stop_and_go.model.phase

data class PhaseState(
    val isGo: Boolean,
    val color: String,
    val durationSeconds: Int
)
