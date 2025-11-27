package com.beavuck.stop_and_go

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.model.TimerConfig
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_COLOR
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_GROWTH_MULTIPLIER
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_STOP_COLOR
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.repositories.ConfigRepository
import org.junit.After
import org.junit.Assert.assertEquals
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
        sharedPreferences = context.getSharedPreferences("stop_and_go_prefs", Context.MODE_PRIVATE)
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
            goDuration = 60,
            stopDuration = 15,
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
}
