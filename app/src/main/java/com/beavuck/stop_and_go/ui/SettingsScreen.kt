package com.beavuck.stop_and_go.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository

@Composable
fun SettingsScreen(
    configRepository: ConfigRepository,
    stateRepository: StateRepository,
    onFinish: () -> Unit,
) {
    val baseContext = LocalContext.current
    val baseConfig = LocalConfiguration.current
    var currentLocale by remember { mutableStateOf(configRepository.loadLocale()) }

    val localizedContext = remember(currentLocale, baseConfig) {
        if (currentLocale.isNullOrEmpty()) {
            baseContext
        } else {
            val locale = java.util.Locale.forLanguageTag(currentLocale!!)
            val newConfig = android.content.res.Configuration(baseConfig)
            newConfig.setLocale(locale)
            baseContext.createConfigurationContext(newConfig)
        }
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        SettingsContent(
            configRepository = configRepository,
            stateRepository = stateRepository,
            currentLocale = currentLocale,
            onLocaleChange = { newLocale ->
                currentLocale = newLocale
                configRepository.saveLocale(newLocale)
            },
            onFinish = onFinish,
        )
    }
}
