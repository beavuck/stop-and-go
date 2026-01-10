package com.beavuck.stop_and_go.ui.tutorial

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.repositories.ConfigRepository

@Composable
fun TutorialScreen(
    configRepository: ConfigRepository,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    onLanguageChanged: () -> Unit
) {
    var currentStep by rememberSaveable {
        mutableIntStateOf(if (configRepository.loadLocale() != null) 1 else 0)
    }

    Surface(modifier = Modifier.fillMaxSize().testTag("tutorialScreen")) {
        when (currentStep) {
            0 -> LanguageSelectionStep(
                onLanguageSelected = { localeCode: SupportedLocale? ->
                    configRepository.saveLocale(localeCode?.code)
                    currentStep++
                    onLanguageChanged()
                },
                onSkip = { onSkip() }
            )

            1 -> GestureDemoStep(
                onComplete = { currentStep++ },
                onSkip = { onSkip() }
            )

            2 -> CreativeUsesStep(
                onNext = { onComplete() },
                onSkip = { onSkip() }
            )
        }
    }
}
