package com.beavuck.stop_and_go.utils.instrumented

import android.graphics.Color
import androidx.core.graphics.toColorInt


object ColorUtils {
    private const val HEX_COLOR_FORMAT = "#%06X"
    private const val COLOR_MASK = 0xFFFFFF
    private const val LIGHT_TEXT_COLOR = 0xFFE0E0E0.toInt()
    private const val DARK_TEXT_COLOR = 0xFF202020.toInt()
    private const val LUMINANCE_THRESHOLD = 0.5f

    const val DEFAULT_COLOR = Color.GRAY

    fun parseColorSafely(colorString: String, defaultColor: Int = DEFAULT_COLOR): Int {
        return isValidColorString(colorString).let {
            if (it) {
                colorString.trim().toColorInt()
            } else {
                defaultColor
            }
        }
    }

    fun isValidColorString(colorString: String): Boolean {
        return try {
            val trimmed = colorString.trim()
            if (trimmed.isEmpty() || isImpossibleHex(trimmed)) {
                return false
            }
            trimmed.toColorInt()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun isImpossibleHex(trimmed: String): Boolean =
        trimmed.startsWith("#")
                && !Regex("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$").matches(trimmed)

    fun getContrastingTextColor(backgroundColor: Int): Int {
        val luminance = Color.luminance(backgroundColor)
        return if (luminance > LUMINANCE_THRESHOLD)
            DARK_TEXT_COLOR
        else
            LIGHT_TEXT_COLOR
    }

    fun getHexFromColorInt(color: Int): String {
        return String.format(HEX_COLOR_FORMAT, COLOR_MASK and color)
    }
}
