package com.beavuck.stop_and_go.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import com.beavuck.stop_and_go.ui.settings.SettingsScreen
import com.beavuck.stop_and_go.config.StopAndGoTheme
import com.beavuck.stop_and_go.utils.instrumented.DebugUtils.maybeSetStrictMode

class SettingsActivity : LocalizedActivity() {
    private lateinit var configRepository: ConfigRepository
    private lateinit var stateRepository: StateRepository
    private lateinit var tutorialRepository: TutorialRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        maybeSetStrictMode()
        super.onCreate(savedInstanceState)

        configRepository = ConfigRepository(this)
        stateRepository = StateRepository(this)
        tutorialRepository = TutorialRepository(this)

        setContent {
            StopAndGoTheme {
                SettingsScreen(
                    configRepository = configRepository,
                    stateRepository = stateRepository,
                    tutorialRepository = tutorialRepository,
                    onFinish = ::finish
                )
            }
        }
    }
}
