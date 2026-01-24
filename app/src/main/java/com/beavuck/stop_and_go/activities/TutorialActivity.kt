package com.beavuck.stop_and_go.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import com.beavuck.stop_and_go.config.StopAndGoTheme
import com.beavuck.stop_and_go.ui.tutorial.TutorialScreen

class TutorialActivity : BeavuckActivity() {
    private lateinit var tutorialRepository: TutorialRepository
    private lateinit var configRepository: ConfigRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tutorialRepository = TutorialRepository(this)
        configRepository = ConfigRepository(this)

        setContent {
            StopAndGoTheme {
                TutorialScreen(
                    configRepository = configRepository,
                    onComplete = ::completeTutorial,
                    onSkip = ::completeTutorial,
                    onLanguageChanged = { recreate() }
                )
            }
        }
    }

    private fun completeTutorial() {
        if (configRepository.loadLocale() == null) {
            configRepository.saveLocale(DEFAULT_LOCALE.code)
        }
        tutorialRepository.markTutorialComplete()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
