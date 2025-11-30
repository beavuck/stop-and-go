package com.beavuck.stop_and_go.settings

import android.widget.EditText
import com.beavuck.stop_and_go.model.TimerConfig
import com.beavuck.stop_and_go.model.TimerConstants

class SettingsInputParser(
    private val goDurationInput: EditText,
    private val stopDurationInput: EditText,
    private val goGrowthInput: EditText,
    private val stopGrowthInput: EditText,
    private val goColorInput: EditText,
    private val stopColorInput: EditText
) {
    fun parseConfig(): TimerConfig {
        val goDuration = goDurationInput.text.toString().toIntOrNull()
        val stopDuration = stopDurationInput.text.toString().toIntOrNull()
        val goGrowth = goGrowthInput.text.toString().toFloatOrNull()
        val stopGrowth = stopGrowthInput.text.toString().toFloatOrNull()
        val goColor = goColorInput.text.toString().trim()
        val stopColor = stopColorInput.text.toString().trim()

        return TimerConfig(
            goDuration = goDuration ?: TimerConstants.DEFAULT_GO_DURATION,
            stopDuration = stopDuration ?: TimerConstants.DEFAULT_STOP_DURATION,
            goDurationGrowth = goGrowth ?: TimerConstants.DEFAULT_GROWTH_MULTIPLIER,
            stopDurationGrowth = stopGrowth ?: TimerConstants.DEFAULT_GROWTH_MULTIPLIER,
            goColor = goColor.ifEmpty { TimerConstants.DEFAULT_GO_COLOR },
            stopColor = stopColor.ifEmpty { TimerConstants.DEFAULT_STOP_COLOR }
        )
    }

    fun loadConfig(config: TimerConfig) {
        goDurationInput.setText(config.goDuration.toString())
        stopDurationInput.setText(config.stopDuration.toString())
        goGrowthInput.setText(config.goDurationGrowth.toString())
        stopGrowthInput.setText(config.stopDurationGrowth.toString())
        goColorInput.setText(config.goColor)
        stopColorInput.setText(config.stopColor)
    }
}
