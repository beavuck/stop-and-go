package com.beavuck.stop_and_go

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.utils.ColorUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorUtilsTest {

    @Test
    fun parseColorSafely_withValidHexColor_returnsCorrectColor() {
        val result = ColorUtils.parseColorSafely("#FF0000")
        assertEquals(Color.RED, result)
    }

    @Test
    fun parseColorSafely_withValidHexColorGreen_returnsCorrectColor() {
        val result = ColorUtils.parseColorSafely("#00FF00")
        assertEquals(Color.GREEN, result)
    }

    @Test
    fun parseColorSafely_withValidHexColorBlue_returnsCorrectColor() {
        val result = ColorUtils.parseColorSafely("#0000FF")
        assertEquals(Color.BLUE, result)
    }

    @Test
    fun parseColorSafely_withEmptyString_returnsDefaultColor() {
        val result = ColorUtils.parseColorSafely("")
        assertEquals(Color.GRAY, result)
    }

    @Test
    fun parseColorSafely_withWhitespace_returnsDefaultColor() {
        val result = ColorUtils.parseColorSafely("   ")
        assertEquals(Color.GRAY, result)
    }

    @Test
    fun parseColorSafely_withInvalidString_returnsDefaultColor() {
        val result = ColorUtils.parseColorSafely("invalid")
        assertEquals(Color.GRAY, result)
    }

    @Test
    fun parseColorSafely_withCustomDefault_returnsCustomDefault() {
        val customDefault = Color.YELLOW
        val result = ColorUtils.parseColorSafely("invalid", customDefault)
        assertEquals(customDefault, result)
    }

    @Test
    fun parseColorSafely_withWhitespaceAroundValid_trimsAndParses() {
        val result = ColorUtils.parseColorSafely("  #FF0000  ")
        assertEquals(Color.RED, result)
    }

    @Test
    fun isValidColorHex_withValidColor_returnsTrue() {
        assertTrue(ColorUtils.isValidColorHex("#FF0000"))
        assertTrue(ColorUtils.isValidColorHex("#00FF00"))
        assertTrue(ColorUtils.isValidColorHex("#0000FF"))
        assertTrue(ColorUtils.isValidColorHex("#ABCDEF"))
        assertTrue(ColorUtils.isValidColorHex("#123456"))
    }

    @Test
    fun isValidColorHex_withValidWordColor_returnsTrue() {
        assertTrue(ColorUtils.isValidColorHex("red"))
        assertTrue(ColorUtils.isValidColorHex("green"))
        assertTrue(ColorUtils.isValidColorHex("blue"))
    }


    @Test
    fun isValidColorHex_withEmptyString_returnsFalse() {
        assertFalse(ColorUtils.isValidColorHex(""))
    }

    @Test
    fun isValidColorHex_withWhitespace_returnsFalse() {
        assertFalse(ColorUtils.isValidColorHex("   "))
    }

    @Test
    fun isValidColorHex_withInvalidString_returnsFalse() {
        assertFalse(ColorUtils.isValidColorHex("invalid"))
        assertFalse(ColorUtils.isValidColorHex("123"))
    }

    @Test
    fun isValidColorHex_withWhitespaceAroundValid_trimsAndValidates() {
        assertTrue(ColorUtils.isValidColorHex("  #FF0000  "))
    }
}
