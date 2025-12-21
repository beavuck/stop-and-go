package com.beavuck.stop_and_go

import android.content.Context
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import org.junit.Assert.assertEquals
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
    fun getDisplayName_returnsStringFromContext() {
        val context = mock(Context::class.java)
        `when`(context.getString(R.string.language_english)).thenReturn("English")

        val displayName = SupportedLocale.ENGLISH.getDisplayName(context)

        assertEquals("English", displayName)
    }

    @Test
    fun getAllDisplayNames_returnsAllLocales() {
        val context = mock(Context::class.java)
        `when`(context.getString(R.string.language_english)).thenReturn("English")
        `when`(context.getString(R.string.language_french)).thenReturn("French")
        `when`(context.getString(R.string.language_arabic)).thenReturn("Arabic")
        `when`(context.getString(R.string.language_chinese_simplified)).thenReturn("Chinese")
        `when`(context.getString(R.string.language_spanish)).thenReturn("Spanish")
        `when`(context.getString(R.string.language_bengali)).thenReturn("Bengali")
        `when`(context.getString(R.string.language_hindi)).thenReturn("Hindi")
        `when`(context.getString(R.string.language_japanese)).thenReturn("Japanese")
        `when`(context.getString(R.string.language_javanese)).thenReturn("Javanese")
        `when`(context.getString(R.string.language_punjabi)).thenReturn("Punjabi")
        `when`(context.getString(R.string.language_portuguese)).thenReturn("Portuguese")
        `when`(context.getString(R.string.language_russian)).thenReturn("Russian")
        `when`(context.getString(R.string.language_turkish)).thenReturn("Turkish")
        `when`(context.getString(R.string.language_vietnamese)).thenReturn("Vietnamese")
        `when`(context.getString(R.string.language_chinese_traditional_hk)).thenReturn("Chinese (Hong Kong)")

        val displayNames = SupportedLocale.getAllDisplayNames(context)

        assertEquals(15, displayNames.size)
        assertEquals("English", displayNames[0])
        assertEquals("French", displayNames[1])
        assertEquals("Arabic", displayNames[2])
        assertEquals("Chinese", displayNames[3])
        assertEquals("Spanish", displayNames[4])
        assertEquals("Bengali", displayNames[5])
        assertEquals("Hindi", displayNames[6])
        assertEquals("Japanese", displayNames[7])
        assertEquals("Javanese", displayNames[8])
        assertEquals("Punjabi", displayNames[9])
        assertEquals("Portuguese", displayNames[10])
        assertEquals("Russian", displayNames[11])
        assertEquals("Turkish", displayNames[12])
        assertEquals("Vietnamese", displayNames[13])
        assertEquals("Chinese (Hong Kong)", displayNames[14])
    }

}
