package com.beavuck.stop_and_go

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.model.timer.TimerConstants.BASE_MAX_STRING_INPUT_LENGTH
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_COLOR
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GROWTH_MULTIPLIER
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_STOP_COLOR
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.repositories.ConfigRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfigRepositoryTest {
    private lateinit var context: Context
    private lateinit var repository: ConfigRepository
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences =
            context.getSharedPreferences(ConfigRepository.PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
        repository = ConfigRepository(context)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun loadConfig_withNoSavedData_returnsDefaultConfig() {
        val config = repository.loadConfig()

        assertEquals(DEFAULT_GO_DURATION, config.goDuration)
        assertEquals(DEFAULT_STOP_DURATION, config.stopDuration)
        assertEquals(DEFAULT_GROWTH_MULTIPLIER, config.goDurationGrowth)
        assertEquals(DEFAULT_GROWTH_MULTIPLIER, config.stopDurationGrowth)
        assertEquals(DEFAULT_GO_COLOR, config.goColor)
        assertEquals(DEFAULT_STOP_COLOR, config.stopColor)
    }

    @Test
    fun saveConfig_persistsAllValues() {
        val customConfig = TimerConfig(
            goDuration = 45,
            stopDuration = 10,
            goDurationGrowth = 1.2f,
            stopDurationGrowth = 1.1f,
            goColor = "#00FF00",
            stopColor = "#FF0000"
        )

        repository.saveConfig(customConfig)
        val loadedConfig = repository.loadConfig()

        assertEquals(customConfig, loadedConfig)
    }

    @Test
    fun saveConfig_thenLoadConfig_returnsExactSameConfig() {
        val config1 = TimerConfig(goDuration = 30, stopDuration = 20)
        repository.saveConfig(config1)

        val config2 = repository.loadConfig()

        assertEquals(config1.goDuration, config2.goDuration)
        assertEquals(config1.stopDuration, config2.stopDuration)
    }

    @Test
    fun saveConfig_overwritesPreviousConfig() {
        val config1 = TimerConfig(goDuration = 30)
        repository.saveConfig(config1)

        val config2 = TimerConfig(goDuration = 90)
        repository.saveConfig(config2)

        val loadedConfig = repository.loadConfig()

        assertEquals(90, loadedConfig.goDuration)
    }

    @Test
    fun saveConfig_withMinValues_persists() {
        val config = TimerConfig(
            goDuration = 1,
            stopDuration = 1,
            goDurationGrowth = 0.01f,
            stopDurationGrowth = 0.01f
        )

        repository.saveConfig(config)
        val loadedConfig = repository.loadConfig()

        assertEquals(config, loadedConfig)
    }

    @Test
    fun saveConfig_withMaxValues_persists() {
        val config = TimerConfig(
            goDuration = 3600,
            stopDuration = 3600,
            goDurationGrowth = 100.0f,
            stopDurationGrowth = 100.0f
        )

        repository.saveConfig(config)
        val loadedConfig = repository.loadConfig()

        assertEquals(config, loadedConfig)
    }

    @Test
    fun newRepositoryInstance_loadsPersistedConfig() {
        val config = TimerConfig(
            goDuration = 120,
            stopDuration = 30,
            goDurationGrowth = 1.5f,
            stopDurationGrowth = 0.9f,
            goColor = "#ABCDEF",
            stopColor = "#123456"
        )
        repository.saveConfig(config)

        val newRepository = ConfigRepository(context)
        val loadedConfig = newRepository.loadConfig()

        assertEquals(config, loadedConfig)
    }

    @Test
    fun saveConfig_withCustomColors_persists() {
        val config = TimerConfig(
            goColor = "#FF5733",
            stopColor = "#33FF57"
        )

        repository.saveConfig(config)
        val loadedConfig = repository.loadConfig()

        assertEquals("#FF5733", loadedConfig.goColor)
        assertEquals("#33FF57", loadedConfig.stopColor)
    }

    @Test
    fun saveConfig_preservesOtherFieldsWhenUpdating() {
        val config1 = TimerConfig(
            goDuration = 100,
            stopDuration = 50,
            goDurationGrowth = 2.0f,
            stopDurationGrowth = 1.5f,
            goColor = "#111111",
            stopColor = "#222222"
        )
        repository.saveConfig(config1)

        val config2 = TimerConfig(
            goDuration = 200,
            stopDuration = 100,
            goDurationGrowth = 1.0f,
            stopDurationGrowth = 1.0f,
            goColor = "#333333",
            stopColor = "#444444"
        )
        repository.saveConfig(config2)

        val loadedConfig = repository.loadConfig()

        assertEquals(200, loadedConfig.goDuration)
        assertEquals(100, loadedConfig.stopDuration)
        assertEquals(1.0f, loadedConfig.goDurationGrowth)
        assertEquals(1.0f, loadedConfig.stopDurationGrowth)
        assertEquals("#333333", loadedConfig.goColor)
        assertEquals("#444444", loadedConfig.stopColor)
    }

    @Test
    fun loadLocale_withNoSavedData_returnsNull() {
        val locale = repository.loadLocale()
        assertEquals(null, locale)
    }

    @Test
    fun saveLocale_persistsLocaleTag() {
        repository.saveLocale("fr")
        val loadedLocale = repository.loadLocale()
        assertEquals("fr", loadedLocale)
    }

    @Test
    fun saveLocale_withEmptyString_persists() {
        repository.saveLocale("")
        val loadedLocale = repository.loadLocale()
        assertEquals("", loadedLocale)
    }

    @Test
    fun saveLocale_overwritesPreviousLocale() {
        repository.saveLocale("en")
        repository.saveLocale("es")
        val loadedLocale = repository.loadLocale()
        assertEquals("es", loadedLocale)
    }

    @Test
    fun saveLocale_withChineseLocale_persists() {
        repository.saveLocale("zh-CN")
        val loadedLocale = repository.loadLocale()
        assertEquals("zh-CN", loadedLocale)
    }

    @Test
    fun newRepositoryInstance_loadsPersistedLocale() {
        repository.saveLocale("ar")
        val newRepository = ConfigRepository(context)
        val loadedLocale = newRepository.loadLocale()
        assertEquals("ar", loadedLocale)
    }

    @Test
    fun saveLocale_independentOfConfig() {
        val config = TimerConfig(goDuration = 100, stopDuration = 50)
        repository.saveConfig(config)
        repository.saveLocale("fr")

        val loadedConfig = repository.loadConfig()
        val loadedLocale = repository.loadLocale()

        assertEquals(100, loadedConfig.goDuration)
        assertEquals("fr", loadedLocale)
    }

    @Test
    fun loadConfig_withNoSavedLabels_returnsEmptyLabels() {
        val config = repository.loadConfig()

        assertEquals("", config.goLabel)
        assertEquals("", config.stopLabel)
    }

    @Test
    fun saveConfig_withCustomLabels_persistsLabels() {
        val config = TimerConfig(
            goLabel = "Sprint",
            stopLabel = "Walk"
        )

        repository.saveConfig(config)
        val loadedConfig = repository.loadConfig()

        assertEquals("Sprint", loadedConfig.goLabel)
        assertEquals("Walk", loadedConfig.stopLabel)
    }

    @Test
    fun listConfigs_MaybeBootstrap_returnsDefaultOnFirstAccess() {
        val configs = repository.listConfigs()

        assertEquals(ConfigRepository.PRESET_COUNT, configs.size)
        assertEquals(context.getString(R.string.config_default_name), configs[0].name)
    }

    @Suppress("DEPRECATION")
    @Test
    fun listConfigs_MaybeBootstrap_migratesLegacyConfigToDefault() {
        sharedPreferences.edit().apply {
            putInt(ConfigRepository.KEY_LEGACY_GO_DURATION, 120)
            putInt(ConfigRepository.KEY_LEGACY_STOP_DURATION, 60)
            putFloat(ConfigRepository.KEY_LEGACY_GO_GROWTH, 1.5f)
            putFloat(ConfigRepository.KEY_LEGACY_STOP_GROWTH, 0.8f)
            putString(ConfigRepository.KEY_LEGACY_GO_COLOR, "#AABBCC")
            putString(ConfigRepository.KEY_LEGACY_STOP_COLOR, "#112233")
            putString(ConfigRepository.KEY_LEGACY_GO_LABEL, "Work")
            putString(ConfigRepository.KEY_LEGACY_STOP_LABEL, "Rest")
            putBoolean(ConfigRepository.KEY_LEGACY_SOUND_ENABLED, false)
            commit()
        }
        val freshRepo = ConfigRepository(context)

        val configs = freshRepo.listConfigs()

        assertEquals(ConfigRepository.PRESET_COUNT, configs.size)
        assertEquals(context.getString(R.string.config_default_name), configs[0].name)
        assertEquals(120, configs[0].config.goDuration)
        assertEquals(60, configs[0].config.stopDuration)
        assertEquals("#AABBCC", configs[0].config.goColor)
        assertEquals("Work", configs[0].config.goLabel)
        assertEquals(false, configs[0].config.soundEnabled)
    }

    @Suppress("DEPRECATION")
    @Test
    fun listConfigs_MaybeBootstrap_migratesLegacyConfig_removesLegacyKeys() {
        sharedPreferences.edit().putInt(ConfigRepository.KEY_LEGACY_GO_DURATION, 99).commit()
        val freshRepo = ConfigRepository(context)
        freshRepo.listConfigs()

        assertEquals(false, sharedPreferences.contains(ConfigRepository.KEY_LEGACY_GO_DURATION))
    }

    @Test
    fun createConfig_addsWithDefaults() {
        repository.bootstrap()
        repository.createConfig("Custom")

        val configs = repository.listConfigs()

        assertEquals(ConfigRepository.PRESET_COUNT + 1, configs.size)
        val custom = configs.first { it.name == "Custom" }
        assertEquals(TimerConfig(), custom.config)
    }

    @Test
    fun createConfig_rejectsDuplicate() {
        repository.bootstrap()

        assertThrows(IllegalArgumentException::class.java) {
            repository.createConfig(context.getString(R.string.config_default_name))
        }
    }

    @Test
    fun createConfig_rejectsEmpty() {
        repository.bootstrap()

        assertThrows(IllegalArgumentException::class.java) {
            repository.createConfig("   ")
        }
    }

    @Test
    fun createConfig_rejectsTooLong() {
        repository.bootstrap()

        assertThrows(IllegalArgumentException::class.java) {
            repository.createConfig("a".repeat(BASE_MAX_STRING_INPUT_LENGTH + 1))
        }
    }

    @Test
    fun renameConfig_updatesName() {
        repository.bootstrap()
        repository.createConfig("Old")
        repository.renameConfig("Old", "New")

        val configs = repository.listConfigs()

        assertEquals(true, configs.any { it.name == "New" })
        assertEquals(false, configs.any { it.name == "Old" })
    }

    @Test
    fun renameConfig_updatesActiveNameWhenRenamingActive() {
        repository.bootstrap()
        val defaultName = context.getString(R.string.config_default_name)
        repository.renameConfig(defaultName, "Renamed")

        assertEquals("Renamed", repository.getActiveConfigName())
    }

    @Test
    fun deleteConfig_rejectsWhenActive() {
        repository.bootstrap()
        val defaultName = context.getString(R.string.config_default_name)

        assertThrows(IllegalStateException::class.java) {
            repository.deleteConfig(defaultName)
        }
    }

    @Test
    fun deleteConfig_removesInactiveConfig() {
        repository.bootstrap()
        repository.createConfig("Temp")
        repository.deleteConfig("Temp")

        val configs = repository.listConfigs()

        assertEquals(false, configs.any { it.name == "Temp" })
    }

    @Test
    fun setActiveConfig_updatesActive() {
        repository.bootstrap()
        repository.createConfig("Other")
        repository.setActiveConfig("Other")

        assertEquals("Other", repository.getActiveConfigName())
    }

    @Test
    fun setActiveConfig_throwsWhenNameNotFound() {
        repository.bootstrap()

        assertThrows(IllegalArgumentException::class.java) {
            repository.setActiveConfig("Nonexistent")
        }
    }

    @Test
    fun saveConfig_updatesOnlyActive() {
        repository.bootstrap()
        repository.createConfig("Second")
        repository.setActiveConfig("Second")
        repository.saveConfig(TimerConfig(goDuration = 999))
        repository.setActiveConfig(context.getString(R.string.config_default_name))

        val defaultConfig = repository.loadConfig()

        assertEquals(DEFAULT_GO_DURATION, defaultConfig.goDuration)
    }

    @Test
    fun saveConfig_withEmptyLabels_persistsEmptyLabels() {
        val config = TimerConfig(
            goLabel = "",
            stopLabel = ""
        )

        repository.saveConfig(config)
        val loadedConfig = repository.loadConfig()

        assertEquals("", loadedConfig.goLabel)
        assertEquals("", loadedConfig.stopLabel)
    }

    @Test
    fun saveConfig_withLongLabels_persistsLongLabels() {
        val longLabel = "a".repeat(32)
        val config = TimerConfig(
            goLabel = longLabel,
            stopLabel = longLabel
        )

        repository.saveConfig(config)
        val loadedConfig = repository.loadConfig()

        assertEquals(longLabel, loadedConfig.goLabel)
        assertEquals(longLabel, loadedConfig.stopLabel)
    }

    @Test
    fun saveConfig_overwritesPreviousLabels() {
        val config1 = TimerConfig(goLabel = "First", stopLabel = "Second")
        repository.saveConfig(config1)

        val config2 = TimerConfig(goLabel = "Third", stopLabel = "Fourth")
        repository.saveConfig(config2)

        val loadedConfig = repository.loadConfig()

        assertEquals("Third", loadedConfig.goLabel)
        assertEquals("Fourth", loadedConfig.stopLabel)
    }

    @Test
    fun saveAndLoadConfig_soundEnabledFalse_persists() {
        repository.saveConfig(TimerConfig(soundEnabled = false))

        val loaded = repository.loadConfig()

        assertEquals(false, loaded.soundEnabled)
    }
}
