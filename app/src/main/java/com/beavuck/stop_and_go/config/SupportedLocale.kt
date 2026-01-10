package com.beavuck.stop_and_go.config

import android.content.Context
import com.beavuck.stop_and_go.R

val DEFAULT_LOCALE = SupportedLocale.ENGLISH

@Suppress("unused", "RedundantSuppression")
enum class SupportedLocale(val code: String, private val nameResId: Int) {
    // ordered by (approximate) number of speakers
    MANDARIN("zh-CN", R.string.language_chinese_simplified),
    SPANISH("es", R.string.language_spanish),
    ENGLISH("en", R.string.language_english),
    HINDI("hi", R.string.language_hindi),
    PORTUGUESE("pt", R.string.language_portuguese),
    BENGALI("bn", R.string.language_bengali),
    RUSSIAN("ru", R.string.language_russian),
    JAPANESE("ja", R.string.language_japanese),
    PUNJABI("pa-PK", R.string.language_punjabi),
    SWAHILI("sw", R.string.language_swahili),
    VIETNAMESE("vi", R.string.language_vietnamese),
    CANTONESE("zh-HK", R.string.language_chinese_traditional_hk),
    TURKISH("tr", R.string.language_turkish),
    ARABIC("ar", R.string.language_arabic),
    WU_CHINESE("wuu-CN", R.string.language_chinese_wu),
    MARATHI("mr", R.string.language_marathi),
    TELUGU("te", R.string.language_telugu),
    KOREAN("ko", R.string.language_korean),
    TAMIL("ta", R.string.language_tamil),
    URDU("ur", R.string.language_urdu),
    GERMAN("de", R.string.language_german),
    INDONESIAN("id", R.string.language_indonesian),
    FRENCH("fr", R.string.language_french),
    JAVANESE("jv", R.string.language_javanese),
    IRANIAN_PERSIAN("fa", R.string.language_iranian_persian),
    ITALIAN("it", R.string.language_italian),
    GUJARATI("gu", R.string.language_gujarati),
    HAUSA("ha", R.string.language_hausa),
    YORUBA("yo", R.string.language_yoruba),
    UKRAINIAN("uk", R.string.language_ukrainian),
    POLISH("pl", R.string.language_polish),
    TAIWANESE_HOKKIEN("zh-TW", R.string.language_chinese_taiwan)
    ;

    fun getDisplayName(context: Context): String {
        return context.getString(nameResId)
    }

    companion object {
        fun fromCode(code: String?): SupportedLocale {
            return entries.find { it.code == code } ?: DEFAULT_LOCALE
        }

        fun getSystemLanguageMatch(context: Context): SupportedLocale {
            val systemLocale = context.resources.configuration.locales[0].toLanguageTag()

            val match = entries.find {
                it.code == systemLocale
                        || systemLocale.split("-")[0] == it.code.split("-")[0]
            }

            return match ?: DEFAULT_LOCALE
        }
    }
}
