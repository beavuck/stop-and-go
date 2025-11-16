package com.beavuck.stop_and_go.model

data class PhaseState(
    val isGo: Boolean,
    val color: String,
    val durationSeconds: Int
)
