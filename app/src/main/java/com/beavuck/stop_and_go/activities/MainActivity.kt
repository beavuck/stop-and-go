package com.beavuck.stop_and_go.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.beavuck.stop_and_go.BuildConfig
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.phase.PhaseManager
import com.beavuck.stop_and_go.notifications.PhaseNotificationManager
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import com.beavuck.stop_and_go.config.StopAndGoTheme
import com.beavuck.stop_and_go.ui.timer.TimerScreen
import com.beavuck.stop_and_go.utils.instrumented.ScreenManager

class MainActivity : BeavuckActivity() {
    private lateinit var phaseManager: PhaseManager
    private lateinit var stateRepository: StateRepository
    private lateinit var notificationManager: PhaseNotificationManager
    private lateinit var screenManager: ScreenManager
    private var currentLocaleTag: String? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tutorialRepository = TutorialRepository(this)
        if (tutorialRepository.shouldShowTutorial()) {
            startActivity(Intent(this, TutorialActivity::class.java))
            finish()
            return
        }

        currentLocaleTag = ConfigRepository(this).loadLocale()
        stateRepository = StateRepository(this)
        notificationManager = PhaseNotificationManager(this)
        notificationManager.createNotificationChannels()
        screenManager = ScreenManager(this)
        requestNotificationPermissionIfNeeded()
        initializePhaseManager()

        setContent {
            StopAndGoTheme {
                TimerScreen(
                    phaseManager = phaseManager,
                    stateRepository = stateRepository,
                    notificationManager = notificationManager,
                    onNavigateToSettings = ::openSettings,
                    onKeepScreenOnChange = { keepOn ->
                        screenManager.setKeepScreenOn(keepOn)
                    }
                )
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (!BuildConfig.DEBUG
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showNotificationPermissionRationale()
        }
    }

    private fun showNotificationPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle(R.string.notification_permission_rationale_title)
            .setMessage(R.string.notification_permission_rationale_message)
            .setPositiveButton(R.string.enable) { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(R.string.not_now, null)
            .show()
    }

    private fun initializePhaseManager() {
        val config = ConfigRepository(this).loadConfig().validate(this)
        phaseManager = PhaseManager(config)
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    public override fun onResume() {
        super.onResume()

        if (stateRepository.isResetPending()) {
            recreate()
            return
        }

        val savedLocale = ConfigRepository(this).loadLocale()
        if (currentLocaleTag != savedLocale) {
            currentLocaleTag = savedLocale
            recreate()
        }
    }
}
