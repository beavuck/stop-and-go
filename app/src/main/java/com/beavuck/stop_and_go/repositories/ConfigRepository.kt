@file:Suppress("RedundantSuppression")

package com.beavuck.stop_and_go.repositories

import android.content.Context
import android.content.SharedPreferences
import com.beavuck.stop_and_go.model.timer.TimerConfig

@Suppress("unused", "RedundantSuppression")
class ConfigRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveConfig(config: TimerConfig) {
        sharedPreferences.edit().apply {
            putInt(KEY_GO_DURATION, config.goDuration)
            putInt(KEY_STOP_DURATION, config.stopDuration)
            putFloat(KEY_GO_GROWTH, config.goDurationGrowth)
            putFloat(KEY_STOP_GROWTH, config.stopDurationGrowth)
            putString(KEY_GO_COLOR, config.goColor)
            putString(KEY_STOP_COLOR, config.stopColor)
            putString(KEY_GO_LABEL, config.goLabel)
            putString(KEY_STOP_LABEL, config.stopLabel)
            apply()
        }
    }

    fun loadConfig(): TimerConfig {
        val defaultConfig = TimerConfig()
        return TimerConfig(
            goDuration = sharedPreferences.getInt(KEY_GO_DURATION, defaultConfig.goDuration),
            stopDuration = sharedPreferences.getInt(KEY_STOP_DURATION, defaultConfig.stopDuration),
            goDurationGrowth = sharedPreferences.getFloat(KEY_GO_GROWTH, defaultConfig.goDurationGrowth),
            stopDurationGrowth = sharedPreferences.getFloat(KEY_STOP_GROWTH, defaultConfig.stopDurationGrowth),
            goColor = getStringFromSharedPrefs(KEY_GO_COLOR, defaultConfig.goColor),
            stopColor = getStringFromSharedPrefs(KEY_STOP_COLOR, defaultConfig.stopColor),
            goLabel = getStringFromSharedPrefs(KEY_GO_LABEL, defaultConfig.goLabel),
            stopLabel = getStringFromSharedPrefs(KEY_STOP_LABEL, defaultConfig.stopLabel)
        )
    }

    private fun getStringFromSharedPrefs(key: String, default: String): String =
        sharedPreferences.getString(key, default) ?: default

    fun saveLocale(localeTag: String) {
        sharedPreferences.edit().apply {
            putString(KEY_LOCALE, localeTag)
            apply()
        }
    }

    fun clearLocale() {
        sharedPreferences.edit().apply {
            remove(KEY_LOCALE)
            apply()
        }
    }

    fun loadLocale(): String? {
        return sharedPreferences.getString(KEY_LOCALE, null)
    }

    fun clearConfig() {
        saveConfig(TimerConfig())
    }

    companion object {
        private const val PREFS_NAME = "stop_and_go_prefs"
        private const val KEY_GO_DURATION = "go_duration"
        private const val KEY_STOP_DURATION = "stop_duration"
        private const val KEY_GO_GROWTH = "go_growth"
        private const val KEY_STOP_GROWTH = "stop_growth"
        private const val KEY_GO_COLOR = "go_color"
        private const val KEY_STOP_COLOR = "stop_color"
        private const val KEY_GO_LABEL = "go_label"
        private const val KEY_STOP_LABEL = "stop_label"
        private const val KEY_LOCALE = "locale"
    }
}
