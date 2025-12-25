package com.beavuck.stop_and_go

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SupportedLocaleTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun fromCode_withNull_returnsDefault() {
        assertEquals(DEFAULT_LOCALE, SupportedLocale.fromCode(null))
    }

    @Test
    fun fromCode_withInvalidCode_returnsDefault() {
        assertEquals(DEFAULT_LOCALE, SupportedLocale.fromCode("invalid"))
        assertEquals(DEFAULT_LOCALE, SupportedLocale.fromCode("pl"))
        assertEquals(DEFAULT_LOCALE, SupportedLocale.fromCode("xyz"))
    }

    @Test
    fun getDisplayName_returnsLocalizedName() {
        val displayName = SupportedLocale.ENGLISH.getDisplayName(context)
        assertNotNull(displayName)
    }

    @Test
    fun entries_containsAllLocales() {
        val entries = SupportedLocale.entries
        assertNotNull(entries.find { it == SupportedLocale.ARABIC })
        assertNotNull(entries.find { it == SupportedLocale.BENGALI })
        assertNotNull(entries.find { it == SupportedLocale.CHINESE_SIMPLIFIED })
        assertNotNull(entries.find { it == SupportedLocale.CHINESE_TRADITIONAL_HK })
        assertNotNull(entries.find { it == SupportedLocale.ENGLISH })
        assertNotNull(entries.find { it == SupportedLocale.FRENCH })
        assertNotNull(entries.find { it == SupportedLocale.HINDI })
        assertNotNull(entries.find { it == SupportedLocale.JAPANESE })
        assertNotNull(entries.find { it == SupportedLocale.JAVANESE })
        assertNotNull(entries.find { it == SupportedLocale.PORTUGUESE })
        assertNotNull(entries.find { it == SupportedLocale.PUNJABI })
        assertNotNull(entries.find { it == SupportedLocale.RUSSIAN })
        assertNotNull(entries.find { it == SupportedLocale.SPANISH })
        assertNotNull(entries.find { it == SupportedLocale.TURKISH })
        assertNotNull(entries.find { it == SupportedLocale.VIETNAMESE })
    }
}
