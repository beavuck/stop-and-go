@file:Suppress("RedundantSuppression")

package com.beavuck.stop_and_go.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.model.timer.NamedConfig
import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.model.timer.TimerConstants.BASE_MAX_STRING_INPUT_LENGTH
import kotlinx.serialization.json.Json

@Suppress("unused", "RedundantSuppression")
class ConfigRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun listConfigs(): List<NamedConfig> {
        bootstrap()
        return readConfigs()
    }

    fun getActiveConfigName(): String {
        bootstrap()
        return sharedPreferences.getString(KEY_ACTIVE_CONFIG_NAME, "") ?: ""
    }

    fun setActiveConfig(name: String) {
        bootstrap()
        val configs = readConfigs()
        require(configs.any { it.name == name }) {
            context.getString(R.string.error_config_name_empty)
        }
        switchConfigTo(name)
    }

    fun createConfig(name: String) {
        bootstrap()
        val configs = readConfigs()
        validateConfigName(name.trim(), configs)
        writeConfigs(configs + NamedConfig(name.trim(), TimerConfig()))
    }

    fun renameConfig(oldName: String, newName: String) {
        bootstrap()
        val trimmed = newName.trim()
        if (oldName == trimmed) return

        val configs = readConfigs()
        validateConfigName(trimmed, configs, excludeName = oldName)
        val updated = configs.map { if (it.name == oldName) it.copy(name = trimmed) else it }
        writeConfigs(updated)
        if (getActiveConfigName() == oldName) {
            setActiveConfig(trimmed)
        }
    }

    fun deleteConfig(name: String) {
        bootstrap()
        check(name != getActiveConfigName()) {
            context.getString(R.string.error_config_name_empty)
        }
        writeConfigs(readConfigs().filter { it.name != name })
    }

    fun updateActiveConfig(config: TimerConfig) {
        bootstrap()
        val activeName = getActiveConfigName()
        val updated = readConfigs().map {
            if (it.name == activeName) it.copy(config = config) else it
        }
        writeConfigs(updated)
    }

    fun saveConfig(config: TimerConfig) {
        updateActiveConfig(config)
    }

    fun loadConfig(): TimerConfig {
        bootstrap()
        val activeName = getActiveConfigName()
        return readConfigs().firstOrNull { it.name == activeName }?.config ?: TimerConfig()
    }

    fun saveLocale(localeTag: String?) {
        sharedPreferences.edit().apply {
            putString(KEY_LOCALE, localeTag ?: DEFAULT_LOCALE.code)
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

    @Suppress("DEPRECATION")
    internal fun bootstrap() {
        if (!sharedPreferences.contains(KEY_CONFIGS_JSON)) {
            val hasLegacy = sharedPreferences.contains(KEY_LEGACY_GO_DURATION)
            val basicConfig = if (hasLegacy) readLegacyConfig() else TimerConfig()
            initPresets(basicConfig)
            if (hasLegacy) removeLegacyKeys()
        }
    }

    fun initPresets(basicConfig: TimerConfig = TimerConfig()) {
        val configs = listOf(
            basicConfig,
            checkersPresetConfig(),
            apneaPresetConfig(),
            hiitPresetConfig(),
            pomodoroPresetConfig()
        )
        writeConfigs(PRESET_NAME_RES_IDS.zip(configs).map { (nameRes, config) ->
            NamedConfig(context.getString(nameRes), config)
        })
        setActiveConfig(context.getString(R.string.config_default_name))
    }

    private fun checkersPresetConfig() = TimerConfig(
        goDuration = 300, stopDuration = 300,
        goColor = "#5C3317", stopColor = "#F0D4A0",
        goLabel = "🔴", stopLabel = "⚫"
    )

    private fun apneaPresetConfig() = TimerConfig(
        goDuration = 90, stopDuration = 90,
        goColor = "#0D3349", stopColor = "#C5E8F0",
        goLabel = "🫧", stopLabel = "🌿"
    )

    private fun hiitPresetConfig() = TimerConfig(
        goDuration = 20, stopDuration = 10,
        goColor = "#1C1C2E", stopColor = "#E3F4FF",
        goLabel = "🔥", stopLabel = "🍃"
    )

    private fun pomodoroPresetConfig() = TimerConfig(
        goDuration = 1500, stopDuration = 300,
        goColor = "#2D5A1B", stopColor = "#2C6FAC",
        goLabel = "🍅", stopLabel = "☁️"
    )

    private fun switchConfigTo(trimmed: String) {
        sharedPreferences.edit { putString(KEY_ACTIVE_CONFIG_NAME, trimmed) }
    }

    @Suppress("DEPRECATION")
    private fun readLegacyConfig(): TimerConfig {
        val defaults = TimerConfig()
        return TimerConfig(
            goDuration = sharedPreferences.getInt(KEY_LEGACY_GO_DURATION, defaults.goDuration),
            stopDuration = sharedPreferences.getInt(
                KEY_LEGACY_STOP_DURATION,
                defaults.stopDuration
            ),
            goDurationGrowth = sharedPreferences.getFloat(
                KEY_LEGACY_GO_GROWTH,
                defaults.goDurationGrowth
            ),
            stopDurationGrowth = sharedPreferences.getFloat(
                KEY_LEGACY_STOP_GROWTH,
                defaults.stopDurationGrowth
            ),
            goColor = sharedPreferences.getString(KEY_LEGACY_GO_COLOR, defaults.goColor)
                ?: defaults.goColor,
            stopColor = sharedPreferences.getString(KEY_LEGACY_STOP_COLOR, defaults.stopColor)
                ?: defaults.stopColor,
            goLabel = sharedPreferences.getString(KEY_LEGACY_GO_LABEL, defaults.goLabel)
                ?: defaults.goLabel,
            stopLabel = sharedPreferences.getString(KEY_LEGACY_STOP_LABEL, defaults.stopLabel)
                ?: defaults.stopLabel,
            soundEnabled = sharedPreferences.getBoolean(
                KEY_LEGACY_SOUND_ENABLED,
                defaults.soundEnabled
            )
        )
    }

    @Suppress("DEPRECATION")
    private fun removeLegacyKeys() {
        sharedPreferences.edit().apply {
            remove(KEY_LEGACY_GO_DURATION)
            remove(KEY_LEGACY_STOP_DURATION)
            remove(KEY_LEGACY_GO_GROWTH)
            remove(KEY_LEGACY_STOP_GROWTH)
            remove(KEY_LEGACY_GO_COLOR)
            remove(KEY_LEGACY_STOP_COLOR)
            remove(KEY_LEGACY_GO_LABEL)
            remove(KEY_LEGACY_STOP_LABEL)
            remove(KEY_LEGACY_SOUND_ENABLED)
            apply()
        }
    }

    private fun readConfigs(): List<NamedConfig> {
        val json = sharedPreferences.getString(KEY_CONFIGS_JSON, "[]") ?: "[]"
        return Json.decodeFromString(json)
    }

    private fun writeConfigs(configs: List<NamedConfig>) {
        sharedPreferences.edit { putString(KEY_CONFIGS_JSON, Json.encodeToString(configs)) }
    }


    private fun validateConfigName(
        name: String,
        configs: List<NamedConfig>,
        excludeName: String? = null
    ) {
        require(name.isNotBlank()) { context.getString(R.string.error_config_name_empty) }
        require(name.length <= BASE_MAX_STRING_INPUT_LENGTH) {
            context.getString(R.string.error_config_name_length, BASE_MAX_STRING_INPUT_LENGTH)
        }
        require(configs.none { it.name == name && it.name != excludeName }) {
            context.getString(R.string.error_config_name_duplicate)
        }
    }

    companion object {
        internal val PRESET_NAME_RES_IDS = listOf(
            R.string.config_default_name,
            R.string.config_preset_checkers,
            R.string.config_preset_apnea,
            R.string.config_preset_hiit,
            R.string.config_preset_pomodoro,
        )
        internal val PRESET_COUNT = PRESET_NAME_RES_IDS.size
        internal const val PREFS_NAME = "stop_and_go_prefs"
        internal const val KEY_CONFIGS_JSON = "configs_json"
        internal const val KEY_ACTIVE_CONFIG_NAME = "active_config_name"
        internal const val KEY_LOCALE = "locale"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_GO_DURATION = "go_duration"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_STOP_DURATION = "stop_duration"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_GO_GROWTH = "go_growth"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_STOP_GROWTH = "stop_growth"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_GO_COLOR = "go_color"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_STOP_COLOR = "stop_color"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_GO_LABEL = "go_label"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_STOP_LABEL = "stop_label"

        @Deprecated(
            "Legacy key from pre-named-configs format. Remove once migration code is deleted.",
            ReplaceWith("KEY_CONFIGS_JSON")
        )
        internal const val KEY_LEGACY_SOUND_ENABLED = "sound_enabled"
    }
}
