package com.beavuck.stop_and_go.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beavuck.stop_and_go.BuildConfig
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.accessibility.AccessibilityHelper
import com.beavuck.stop_and_go.gestures.TimerGestureHandler
import com.beavuck.stop_and_go.model.PhaseManager
import com.beavuck.stop_and_go.model.PhaseState
import com.beavuck.stop_and_go.notifications.PhaseNotificationManager
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.timer.TimerController
import com.beavuck.stop_and_go.ui.TimerUIUpdater
import com.beavuck.stop_and_go.utils.DebugUtils.maybeSetStrictMode
import com.beavuck.stop_and_go.utils.ScreenManager

class MainActivity : LocalizedActivity() {
    private lateinit var phaseManager: PhaseManager
    private lateinit var stateRepository: StateRepository
    private lateinit var notificationManager: PhaseNotificationManager
    private lateinit var timerController: TimerController
    private lateinit var uiUpdater: TimerUIUpdater
    private lateinit var accessibilityHelper: AccessibilityHelper
    private lateinit var gestureHandler: TimerGestureHandler
    private lateinit var screenManager: ScreenManager

    private var secondsRemaining: Int = 0
    private var isPaused: Boolean = false
    private var currentLocaleTag: String? = null

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var timerText: TextView
    private lateinit var cycleCountText: TextView
    private lateinit var phaseLabelText: TextView

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        maybeSetStrictMode()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        currentLocaleTag = ConfigRepository(this).loadLocale()

        initializeViews()
        initializeHelpers()
        setupUI()

        stateRepository = StateRepository(this)
        notificationManager = PhaseNotificationManager(this)
        notificationManager.createNotificationChannels()
        requestNotificationPermissionIfNeeded()
        initializePhaseManager()
        restoreSavedState()
        startCurrentPhase()
    }

    private fun initializeViews() {
        mainLayout = findViewById(R.id.main)
        timerText = findViewById(R.id.timerText)
        cycleCountText = findViewById(R.id.cycleCount)
        phaseLabelText = findViewById(R.id.phaseLabel)
    }

    private fun initializeHelpers() {
        timerController = TimerController(
            onTickCallback = ::handleTimerTick,
            onFinishCallback = ::handleTimerFinish
        )
        uiUpdater = TimerUIUpdater(this, mainLayout, timerText, phaseLabelText, cycleCountText)
        accessibilityHelper = AccessibilityHelper(this, mainLayout, phaseLabelText)
        gestureHandler = TimerGestureHandler(
            context = this,
            onSingleTap = ::togglePause,
            onLongPress = ::openSettings
        )
        screenManager = ScreenManager(this)
    }

    private fun setupUI() {
        setupEdgeToEdge()
        accessibilityHelper.setupAccessibilityActions { isPaused }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            timerController.cancel()
        } else {
            timerController.start(secondsRemaining)
        }
        accessibilityHelper.announcePauseState(isPaused)
        screenManager.setKeepScreenOn(!isPaused)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        gestureHandler.gestureDetector.onTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    private fun initializePhaseManager() {
        val config = ConfigRepository(this).loadConfig().validate()
        phaseManager = PhaseManager(config)
    }

    private fun restoreSavedState() {
        val savedState = stateRepository.loadState()
        if (savedState != null) {
            phaseManager.restoreState(savedState)
            secondsRemaining = savedState.secondsRemaining
        } else {
            secondsRemaining = 0
            isPaused = false
        }
    }

    private fun openSettings() {
        saveCurrentState()
        timerController.cancel()
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun startCurrentPhase(notify: Boolean = false) {
        val phase = phaseManager.getCurrentPhase()
        val locale = resources.configuration.locales[0]
        uiUpdater.updatePhase(phase, phaseManager.cycleCount, locale)
        if (notify) {
            notifyPhaseChange(phase)
            accessibilityHelper.announcePhaseChange(phase)
        }
        timerController.start(getTimerDuration(phase))
        screenManager.setKeepScreenOn(true)
    }

    private fun notifyPhaseChange(phase: PhaseState) {
        if (phase.isGo) {
            notificationManager.notifyGoPhase()
        } else {
            notificationManager.notifyStopPhase()
        }
    }

    private fun getTimerDuration(phase: PhaseState): Int {
        if (secondsRemaining > 0) {
            val duration = secondsRemaining
            secondsRemaining = 0
            return duration
        }
        return phase.durationSeconds
    }

    private fun handleTimerTick(remaining: Int) {
        val locale = resources.configuration.locales[0]
        uiUpdater.updateTimerDisplay(remaining, locale)
        secondsRemaining = remaining
    }

    private fun handleTimerFinish() {
        secondsRemaining = 0
        phaseManager.advanceToNextPhase()
        startCurrentPhase(notify = true)
    }

    private fun saveCurrentState() {
        val state = phaseManager.getState().copy(secondsRemaining = secondsRemaining)
        stateRepository.saveState(state)
    }

    public override fun onResume() {
        super.onResume()

        val savedLocale = ConfigRepository(this).loadLocale()
        if (currentLocaleTag != savedLocale) {
            currentLocaleTag = savedLocale
            recreate()
            return
        }

        initializePhaseManager()
        restoreSavedState()
        startCurrentPhase()
        if (!isPaused) {
            screenManager.setKeepScreenOn(true)
        }
    }

    override fun onPause() {
        super.onPause()
        saveCurrentState()
        screenManager.setKeepScreenOn(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerController.cancel()
    }
}
