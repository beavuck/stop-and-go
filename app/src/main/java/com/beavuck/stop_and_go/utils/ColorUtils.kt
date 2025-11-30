package com.beavuck.stop_and_go.utils

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.beavuck.stop_and_go.model.TimerConstants.COLOR_MASK
import com.beavuck.stop_and_go.model.TimerConstants.HEX_COLOR_FORMAT

object ColorUtils {

    private const val LIGHT_TEXT_COLOR = 0xFFE0E0E0.toInt()
    private const val DARK_TEXT_COLOR = 0xFF202020.toInt()
    private const val LUMINANCE_THRESHOLD = 0.5f

    fun parseColorSafely(colorHex: String, defaultColor: Int = Color.GRAY): Int {
        return try {
            val trimmed = colorHex.trim()
            if (trimmed.isEmpty()) {
                defaultColor
            } else {
                trimmed.toColorInt()
            }
        } catch (_: Exception) {
            defaultColor
        }
    }

    fun isValidColorHex(colorHex: String): Boolean {
        return try {
            val trimmed = colorHex.trim()
            if (trimmed.isEmpty()) return false
            trimmed.toColorInt()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun getContrastingTextColor(backgroundColor: Int): Int {
        val luminance = Color.luminance(backgroundColor)
        return if (luminance > LUMINANCE_THRESHOLD)
            DARK_TEXT_COLOR
        else
            LIGHT_TEXT_COLOR
    }

    fun colorToHex(color: Int): String {
        return String.format(HEX_COLOR_FORMAT, COLOR_MASK and color)
    }
}
