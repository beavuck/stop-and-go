package com.beavuck.stop_and_go.ui

import android.content.Context
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.PhaseState
import com.beavuck.stop_and_go.utils.ColorUtils
import java.text.NumberFormat
import java.util.Locale

class TimerUIUpdater(
    private val context: Context,
    private val mainLayout: ConstraintLayout,
    private val timerText: TextView,
    private val phaseLabelText: TextView,
    private val cycleCountText: TextView
) {
    fun updatePhase(phase: PhaseState, cycleCount: Int, locale: Locale) {
        val backgroundColor = phase.color.toColorInt()
        val textColor = ColorUtils.getContrastingTextColor(backgroundColor)

        mainLayout.setBackgroundColor(backgroundColor)
        timerText.setTextColor(textColor)
        phaseLabelText.setTextColor(textColor)
        cycleCountText.setTextColor(textColor)

        phaseLabelText.text = getPhaseLabel(phase)
        timerText.text = formatNumber(phase.durationSeconds, locale)
        cycleCountText.text = context.getString(R.string.cycle_count, cycleCount + 1)
    }

    fun updateTimerDisplay(seconds: Int, locale: Locale) {
        timerText.text = formatNumber(seconds, locale)
    }

    private fun getPhaseLabel(phase: PhaseState): String {
        return if (phase.isGo) {
            context.getString(R.string.phase_go)
        } else {
            context.getString(R.string.phase_stop)
        }
    }

    private fun formatNumber(number: Int, locale: Locale): String {
        return NumberFormat.getIntegerInstance(locale).format(number)
    }
}
