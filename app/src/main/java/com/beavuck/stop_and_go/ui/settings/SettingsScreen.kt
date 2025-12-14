package com.beavuck.stop_and_go.ui.settings

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import java.util.Locale

@Composable
fun SettingsScreen(
    configRepository: ConfigRepository,
    stateRepository: StateRepository,
    tutorialRepository: TutorialRepository,
    onFinish: () -> Unit,
) {
    val baseContext = LocalContext.current
    val baseConfig = LocalConfiguration.current
    var currentLocale by remember { mutableStateOf(configRepository.loadLocale()) }

    val localizedContext = remember(currentLocale, baseConfig) {
        if (currentLocale.isNullOrEmpty()) {
            baseContext
        } else {
            val locale = Locale.forLanguageTag(currentLocale!!)
            val newConfig = Configuration(baseConfig)
            newConfig.setLocale(locale)
            baseContext.createConfigurationContext(newConfig)
        }
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        SettingsContent(
            configRepository = configRepository,
            stateRepository = stateRepository,
            tutorialRepository = tutorialRepository,
            currentLocale = currentLocale,
            onLocaleChange = { newLocale ->
                currentLocale = newLocale
                configRepository.saveLocale(newLocale)
            },
            onFinish = onFinish,
        )
    }
}
