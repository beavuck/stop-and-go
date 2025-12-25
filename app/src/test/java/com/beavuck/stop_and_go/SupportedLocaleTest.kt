package com.beavuck.stop_and_go

import android.content.Context
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SupportedLocaleTest {

    @Test
    fun default_isEnglish() {
        assertEquals(SupportedLocale.ENGLISH, DEFAULT_LOCALE)
    }

    @Test
    fun fromCode_withEn_returnsEnglish() {
        val result = SupportedLocale.fromCode("en")
        assertEquals(SupportedLocale.ENGLISH, result)
    }

    @Test
    fun fromCode_withNull_returnsDefault() {
        val result = SupportedLocale.fromCode(null)
        assertEquals(DEFAULT_LOCALE, result)
    }

    @Test
    fun fromCode_withInvalidCode_returnsDefault() {
        val result = SupportedLocale.fromCode("invalid")
        assertEquals(DEFAULT_LOCALE, result)
    }

    @Test
    fun fromCode_withEmptyString_returnsDefault() {
        val result = SupportedLocale.fromCode("")
        assertEquals(DEFAULT_LOCALE, result)
    }

    @Test
    fun getAllDisplayNames_returnsAllLocales() {
        val context = mock(Context::class.java)
        `when`(context.getString(R.string.language_arabic)).thenReturn("Arabic")
        `when`(context.getString(R.string.language_bengali)).thenReturn("Bengali")
        `when`(context.getString(R.string.language_chinese_simplified)).thenReturn("Chinese")
        `when`(context.getString(R.string.language_chinese_traditional_hk)).thenReturn("Chinese (Hong Kong)")
        `when`(context.getString(R.string.language_english)).thenReturn("English")
        `when`(context.getString(R.string.language_french)).thenReturn("French")
        `when`(context.getString(R.string.language_hindi)).thenReturn("Hindi")
        `when`(context.getString(R.string.language_japanese)).thenReturn("Japanese")
        `when`(context.getString(R.string.language_javanese)).thenReturn("Javanese")
        `when`(context.getString(R.string.language_portuguese)).thenReturn("Portuguese")
        `when`(context.getString(R.string.language_punjabi)).thenReturn("Punjabi")
        `when`(context.getString(R.string.language_russian)).thenReturn("Russian")
        `when`(context.getString(R.string.language_spanish)).thenReturn("Spanish")
        `when`(context.getString(R.string.language_turkish)).thenReturn("Turkish")
        `when`(context.getString(R.string.language_vietnamese)).thenReturn("Vietnamese")

        val displayNames = SupportedLocale.getAllDisplayNames(context)

        assertNotNull(displayNames.find { it == "Arabic" })
        assertNotNull(displayNames.find { it == "Bengali" })
        assertNotNull(displayNames.find { it == "Chinese" })
        assertNotNull(displayNames.find { it == "Chinese (Hong Kong)" })
        assertNotNull(displayNames.find { it == "English" })
        assertNotNull(displayNames.find { it == "French" })
        assertNotNull(displayNames.find { it == "Hindi" })
        assertNotNull(displayNames.find { it == "Japanese" })
        assertNotNull(displayNames.find { it == "Javanese" })
        assertNotNull(displayNames.find { it == "Portuguese" })
        assertNotNull(displayNames.find { it == "Punjabi" })
        assertNotNull(displayNames.find { it == "Russian" })
        assertNotNull(displayNames.find { it == "Spanish" })
        assertNotNull(displayNames.find { it == "Turkish" })
        assertNotNull(displayNames.find { it == "Vietnamese" })
    }

}
