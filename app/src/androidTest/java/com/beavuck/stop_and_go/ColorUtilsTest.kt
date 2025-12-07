package com.beavuck.stop_and_go

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils.DEFAULT_COLOR
import org.junit.Assert.*
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
    fun parseColorSafely_withHexLikeImpossibleHex_returnsDefaultColor() {
        val result = ColorUtils.parseColorSafely("#GGHHII")
        assertEquals(DEFAULT_COLOR, result)
    }

    @Test
    fun parseColorSafely_withValidHexColorGreen_returnsCorrectColor() {
        val result = ColorUtils.parseColorSafely("#00FF00")
        assertEquals(Color.GREEN, result)
    }

    @Test
    fun parseColorSafely_withValidWordColorGreen_returnsCorrectColor() {
        val result = ColorUtils.parseColorSafely("green")
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
        assertEquals(DEFAULT_COLOR, result)
    }

    @Test
    fun parseColorSafely_withWhitespace_returnsDefaultColor() {
        val result = ColorUtils.parseColorSafely("   ")
        assertEquals(DEFAULT_COLOR, result)
    }

    @Test
    fun parseColorSafely_withInvalidString_returnsDefaultColor() {
        val result = ColorUtils.parseColorSafely("invalid")
        assertEquals(DEFAULT_COLOR, result)
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
        assertTrue(ColorUtils.isValidColorString("#FF0000"))
        assertTrue(ColorUtils.isValidColorString("#00FF00"))
        assertTrue(ColorUtils.isValidColorString("#0000FF"))
        assertTrue(ColorUtils.isValidColorString("#ABCDEF"))
        assertTrue(ColorUtils.isValidColorString("#123456"))
    }

    @Test
    fun isValidColorHex_withValidWordColor_returnsTrue() {
        assertTrue(ColorUtils.isValidColorString("red"))
        assertTrue(ColorUtils.isValidColorString("green"))
        assertTrue(ColorUtils.isValidColorString("blue"))
    }


    @Test
    fun isValidColorString_withEmptyString_returnsFalse() {
        assertFalse(ColorUtils.isValidColorString(""))
    }

    @Test
    fun isValidColorString_withWhitespace_returnsFalse() {
        assertFalse(ColorUtils.isValidColorString("   "))
    }

    @Test
    fun isValidColorString_withInvalidString_returnsFalse() {
        assertFalse(ColorUtils.isValidColorString("invalid"))
        assertFalse(ColorUtils.isValidColorString("123"))
    }

    @Test
    fun isValidColorHex_withWhitespaceAroundValid_trimsAndValidates() {
        assertTrue(ColorUtils.isValidColorString("  #FF0000  "))
    }

    @Test
    fun getContrastingTextColor_withWhiteBackground_returnsDarkText() {
        val result = ColorUtils.getContrastingTextColor(Color.WHITE)
        assertTrue(Color.luminance(result) < 0.5)
    }

    @Test
    fun getContrastingTextColor_withBlackBackground_returnsLightText() {
        val result = ColorUtils.getContrastingTextColor(Color.BLACK)
        assertTrue(Color.luminance(result) > 0.5)
    }

    @Test
    fun getContrastingTextColor_withLightBackground_returnsDarkText() {
        val lightColor = Color.rgb(200, 200, 200)
        val result = ColorUtils.getContrastingTextColor(lightColor)
        assertTrue(Color.luminance(result) < 0.5)
    }

    @Test
    fun getContrastingTextColor_withDarkBackground_returnsLightText() {
        val darkColor = Color.rgb(50, 50, 50)
        val result = ColorUtils.getContrastingTextColor(darkColor)
        assertTrue(Color.luminance(result) > 0.5)
    }

    @Test
    fun getContrastingTextColor_withBrightRedBackground_returnsLightText() {
        val result = ColorUtils.getContrastingTextColor(Color.RED)
        assertTrue(Color.luminance(result) > 0.5)
    }

    @Test
    fun getContrastingTextColor_withDarkBlueBackground_returnsLightText() {
        val darkBlue = Color.rgb(0, 0, 139)
        val result = ColorUtils.getContrastingTextColor(darkBlue)
        assertTrue(Color.luminance(result) > 0.5)
    }

    @Test
    fun getContrastingTextColor_withDefaultGoColor_returnsLightText() {
        val goColor = Color.parseColor("#20b05c")
        val result = ColorUtils.getContrastingTextColor(goColor)
        assertTrue(Color.luminance(result) > 0.5)
    }

    @Test
    fun getContrastingTextColor_withDefaultStopColor_returnsLightText() {
        val stopColor = Color.parseColor("#992639")
        val result = ColorUtils.getContrastingTextColor(stopColor)
        assertTrue(Color.luminance(result) > 0.5)
    }
}
