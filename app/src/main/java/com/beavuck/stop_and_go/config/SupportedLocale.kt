package com.beavuck.stop_and_go.config

import android.content.Context
import com.beavuck.stop_and_go.R

val DEFAULT_LOCALE = SupportedLocale.ENGLISH

@Suppress("unused", "RedundantSuppression")
enum class SupportedLocale(val code: String, private val nameResId: Int) {
    // ordered by (approximate) number of speakers, then grouped by language, as of what wikipedia said in 2026
    ENGLISH("en", R.string.language_english), // 1.5B
    BRITISH_ENGLISH("en-GB", R.string.language_british_english), // 1.5B
    MANDARIN("zh-CN", R.string.language_chinese_simplified), // 1.2B
    YUE_CHINESE("zh-HK", R.string.language_chinese_traditional_hk),
    WU_CHINESE("wuu-CN", R.string.language_chinese_wu),
    TAIWANESE_HOKKIEN("zh-TW", R.string.language_chinese_taiwan),
    HINDI("hi", R.string.language_hindi), //611M
    SPANISH("es", R.string.language_spanish), //561M
    MEXICAN_SPANISH("es-MX", R.string.language_mexican_spanish),
    ARABIC("ar", R.string.language_arabic), //335M
    EGYPTIAN_ARABIC("ar-EG", R.string.language_egyptian_arabic),
    ALGERIAN_ARABIC("ar-DZ", R.string.language_algerian_arabic),
    LEBANESE_ARABIC("ar-LB", R.string.language_lebanese_arabic),
    SUDANESE_ARABIC("ar-SD", R.string.language_sudanese_arabic),
    IRAQI_ARABIC("ar-IQ", R.string.language_iraqi_arabic),
    FRENCH("fr", R.string.language_french), // 334M
    BENGALI("bn", R.string.language_bengali), // 274M
    PORTUGUESE("pt", R.string.language_portuguese),
    BRAZILIAN_PORTUGUESE("pt-BR", R.string.language_brazilian_portuguese), //269M
    INDONESIAN("id", R.string.language_indonesian), // 255M
    URDU("ur", R.string.language_urdu), // 246M
    RUSSIAN("ru", R.string.language_russian), // 210M
    GERMAN("de", R.string.language_german), // 131M
    JAPANESE("ja", R.string.language_japanese), // 126M
    NIGERIAN_PIDGIN("pcm-NG", R.string.language_nigerian_pidgin), // 121M
    MARATHI("mr", R.string.language_marathi), // 99M
    VIETNAMESE("vi", R.string.language_vietnamese), // 97M
    TELUGU("te", R.string.language_telugu), // 96M
    SWAHILI("sw", R.string.language_swahili), // 95M
    HAUSA("ha", R.string.language_hausa), // 94M
    TURKISH("tr", R.string.language_turkish), // 94M
    WESTERN_PUNJABI("pa-PK", R.string.language_western_punjabi), // 90M
    TAGALOG("tl", R.string.language_tagalog), // 87M
    TAMIL("ta", R.string.language_tamil), // 86M
    IRANIAN_PERSIAN("fa", R.string.language_iranian_persian), // 82M
    KOREAN("ko", R.string.language_korean), // 82M
    AMHARIC("am", R.string.language_amharic), // 78M
    THAI("th", R.string.language_thai), // 71M
    JAVANESE("jv", R.string.language_javanese), // 69M
    ITALIAN("it", R.string.language_italian), // 66M
    GUJARATI("gu", R.string.language_gujarati), // 62M
    KANNADA("kn", R.string.language_kannada), // 59M
    YORUBA("yo", R.string.language_yoruba), // 53M
    BHOJPURI("bho", R.string.language_bhojpuri), // 53M
    POLISH("pl", R.string.language_polish), // 40M
    UKRAINIAN("uk", R.string.language_ukrainian), //39M
    ROMANIAN("ro", R.string.language_romanian), // 27M
    HUNGARIAN("hu", R.string.language_hungarian), // 14M
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
