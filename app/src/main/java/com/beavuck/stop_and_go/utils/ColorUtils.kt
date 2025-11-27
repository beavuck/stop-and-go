package com.beavuck.stop_and_go.utils

import android.graphics.Color
import androidx.core.graphics.toColorInt

object ColorUtils {

    fun parseColorSafely(colorHex: String, defaultColor: Int = Color.GRAY): Int {
        return try {
            val trimmed = colorHex.trim()
            if (trimmed.isEmpty()) {
                defaultColor
            } else {
                trimmed.toColorInt()
            }
        } catch (e: Exception) {
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
}
