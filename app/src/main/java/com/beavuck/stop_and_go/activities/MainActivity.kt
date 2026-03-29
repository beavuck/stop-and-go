package com.beavuck.stop_and_go.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.beavuck.stop_and_go.config.StopAndGoTheme
import com.beavuck.stop_and_go.dialogs.SoundMigrationDialog
import com.beavuck.stop_and_go.model.phase.PhaseManager
import com.beavuck.stop_and_go.repositories.AnnouncementsRepository
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import com.beavuck.stop_and_go.sounds.PhaseSoundPlayer
import com.beavuck.stop_and_go.ui.timer.TimerScreen
import com.beavuck.stop_and_go.utils.instrumented.ScreenManager

class MainActivity : BeavuckActivity() {
    private lateinit var phaseManager: PhaseManager
    private lateinit var stateRepository: StateRepository
    private lateinit var soundPlayer: PhaseSoundPlayer
    private lateinit var screenManager: ScreenManager
    private var currentLocaleTag: String? = null
    private val soundEnabled = mutableStateOf(false)
    private val showSoundMigrationDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tutorialRepository = TutorialRepository(this)
        if (tutorialRepository.shouldShowTutorial()) {
            startActivity(Intent(this, TutorialActivity::class.java))
            finish()
            return
        }

        val configRepository = ConfigRepository(this)
        currentLocaleTag = configRepository.loadLocale()
        stateRepository = StateRepository(this)
        soundPlayer = PhaseSoundPlayer()
        screenManager = ScreenManager(this)
        soundEnabled.value = configRepository.loadConfig().soundEnabled
        initializePhaseManager()

        // (2026-10): Remove this block (sound migration announcement)
        val announcementsRepository = AnnouncementsRepository(this)
        if (announcementsRepository.shouldShowSoundMigrationAnnouncement()) {
            showSoundMigrationDialog.value = true
            announcementsRepository.markSoundMigrationAnnouncementShown()
        }

        setContent {
            StopAndGoTheme {
                TimerScreen(
                    phaseManager = phaseManager,
                    stateRepository = stateRepository,
                    soundPlayer = soundPlayer,
                    soundEnabled = soundEnabled.value,
                    onNavigateToSettings = ::openSettings,
                    onKeepScreenOnChange = { keepOn ->
                        screenManager.setKeepScreenOn(keepOn)
                    }
                )

                if (showSoundMigrationDialog.value) {
                    SoundMigrationDialog(onDismiss = { showSoundMigrationDialog.value = false })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::soundPlayer.isInitialized) soundPlayer.release()
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

        val configRepository = ConfigRepository(this)
        soundEnabled.value = configRepository.loadConfig().soundEnabled

        val savedLocale = configRepository.loadLocale()
        if (currentLocaleTag != savedLocale) {
            currentLocaleTag = savedLocale
            recreate()
        }
    }
}
