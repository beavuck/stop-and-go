package com.beavuck.stop_and_go.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.ui.SettingsScreen
import com.beavuck.stop_and_go.ui.StopAndGoTheme
import com.beavuck.stop_and_go.utils.instrumented.DebugUtils.maybeSetStrictMode

class SettingsActivity : LocalizedActivity() {
    private lateinit var configRepository: ConfigRepository
    private lateinit var stateRepository: StateRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        maybeSetStrictMode()
        super.onCreate(savedInstanceState)

        configRepository = ConfigRepository(this)
        stateRepository = StateRepository(this)

        setContent {
            StopAndGoTheme {
                SettingsScreen(
                    configRepository = configRepository,
                    stateRepository = stateRepository,
                    onFinish = ::finish
                )
            }
        }
    }
}
