package com.beavuck.stop_and_go

import com.beavuck.stop_and_go.model.timer.NamedConfig
import com.beavuck.stop_and_go.model.timer.TimerConfig
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class NamedConfigSerializationTest {

    @Test
    fun namedConfig_roundTrip_preservesAllFields() {
        val original = NamedConfig(
            name = "Checkers Timer",
            config = TimerConfig(
                goDuration = 300,
                stopDuration = 300,
                goLabel = "Red",
                stopLabel = "Black"
            )
        )

        val json = Json.encodeToString(original)
        val restored = Json.decodeFromString<NamedConfig>(json)

        assertEquals(original, restored)
    }

    @Test
    fun namedConfigList_roundTrip_preservesAllEntries() {
        val configs = listOf(
            NamedConfig("Basic", TimerConfig()),
            NamedConfig(
                name = "Checkers Timer",
                config = TimerConfig(
                    goDuration = 300,
                    stopDuration = 300,
                    goLabel = "Red",
                    stopLabel = "Black"
                )
            )
        )

        val json = Json.encodeToString(configs)
        val restored = Json.decodeFromString<List<NamedConfig>>(json)

        assertEquals(configs, restored)
    }
}
