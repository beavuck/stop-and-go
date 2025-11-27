package com.beavuck.stop_and_go.model

import android.content.Context
import com.beavuck.stop_and_go.R

val DEFAULT = SupportedLocale.ENGLISH

enum class SupportedLocale(val code: String, private val nameResId: Int) {
    ENGLISH("en", R.string.language_english),
    FRENCH("fr", R.string.language_french),
    ARABIC("ar", R.string.language_arabic),
    CHINESE_SIMPLIFIED("zh-CN", R.string.language_chinese_simplified),
    SPANISH("es", R.string.language_spanish);

    fun getDisplayName(context: Context): String {
        return context.getString(nameResId)
    }

    companion object {
        fun fromCode(code: String?): SupportedLocale {
            return entries.find { it.code == code } ?: DEFAULT
        }

        fun getAllDisplayNames(context: Context): Array<String> {
            return entries.map { it.getDisplayName(context) }.toTypedArray()
        }
    }
}
