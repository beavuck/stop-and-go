package com.beavuck.stop_and_go.model.timer

import kotlinx.serialization.Serializable

@Serializable
data class NamedConfig(val name: String, val config: TimerConfig)
