package com.beavuck.stop_and_go.config

import android.content.Context
import com.beavuck.stop_and_go.R

val DEFAULT_LOCALE = SupportedLocale.ENGLISH

@Suppress("unused", "RedundantSuppression")
enum class SupportedLocale(val code: String, private val nameResId: Int) {
    CHINESE_SIMPLIFIED("zh-CN", R.string.language_chinese_simplified),
    SPANISH("es", R.string.language_spanish),
    ENGLISH("en", R.string.language_english),
    HINDI("hi", R.string.language_hindi),
    PORTUGUESE("pt", R.string.language_portuguese),
    BENGALI("bn", R.string.language_bengali),
    RUSSIAN("ru", R.string.language_russian),
    JAPANESE("ja", R.string.language_japanese),
    PUNJABI("pa-PK", R.string.language_punjabi),
    VIETNAMESE("vi", R.string.language_vietnamese),
    CHINESE_TRADITIONAL_HK("zh-HK", R.string.language_chinese_traditional_hk),
    TURKISH("tr", R.string.language_turkish),
    ARABIC("ar", R.string.language_arabic),
    CHINESE_WU_SH("wuu-rCN", R.string.language_chinese_wu),
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
    HAUSA("ha", R.string.language_hausa),
    GUJARATI("gu", R.string.language_gujarati),
    POLISH("pl", R.string.language_polish),
    ;

    fun getDisplayName(context: Context): String {
        return context.getString(nameResId)
    }

    companion object {
        fun fromCode(code: String?): SupportedLocale {
            return entries.find { it.code == code } ?: DEFAULT_LOCALE
        }

        fun getAllDisplayNames(context: Context): Array<String> {
            return entries.map { it.getDisplayName(context) }.toTypedArray()
        }
    }
}
