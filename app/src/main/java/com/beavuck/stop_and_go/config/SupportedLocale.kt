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
    FRENCH("fr", R.string.language_french),
    JAVANESE("jv", R.string.language_javanese);

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
