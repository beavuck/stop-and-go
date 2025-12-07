package com.beavuck.stop_and_go

import android.content.Context
import com.beavuck.stop_and_go.model.DEFAULT_LOCALE
import com.beavuck.stop_and_go.model.SupportedLocale
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

        val displayNames = SupportedLocale.getAllDisplayNames(context)

        assertEquals(5, displayNames.size)
        assertEquals("English", displayNames[0])
        assertEquals("French", displayNames[1])
        assertEquals("Arabic", displayNames[2])
        assertEquals("Chinese", displayNames[3])
        assertEquals("Spanish", displayNames[4])
    }

}
