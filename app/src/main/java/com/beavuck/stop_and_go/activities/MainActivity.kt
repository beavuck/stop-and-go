package com.beavuck.stop_and_go.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.PhaseManager
import com.beavuck.stop_and_go.model.PhaseState
import com.beavuck.stop_and_go.model.TimerConstants.MILLIS_PER_SECOND
import com.beavuck.stop_and_go.model.TimerConstants.TIMER_DISPLAY_OFFSET
import com.beavuck.stop_and_go.notifications.PhaseNotificationManager
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import java.text.NumberFormat

class MainActivity : LocalizedActivity() {
    private lateinit var phaseManager: PhaseManager
    private lateinit var stateRepository: StateRepository
    private lateinit var notificationManager: PhaseNotificationManager
    private lateinit var gestureDetector: GestureDetector
    private var currentTimer: CountDownTimer? = null
    private var secondsRemaining: Int = 0
    private var isPaused: Boolean = false
    private var currentLocaleTag: String? = null

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var timerText: TextView
    private lateinit var cycleCountText: TextView
    private lateinit var phaseLabelText: TextView

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Permission denied => do nothing
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        currentLocaleTag = ConfigRepository(this).loadLocale()

        bindViews()
        setupEdgeToEdge()
        setupGestureDetection()

        stateRepository = StateRepository(this)
        notificationManager = PhaseNotificationManager(this)
        notificationManager.createNotificationChannels()
        requestNotificationPermissionIfNeeded()
        initializePhaseManager()
        restoreSavedState()
        startCurrentPhase()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun bindViews() {
        mainLayout = findViewById(R.id.main)
        timerText = findViewById(R.id.timerText)
        cycleCountText = findViewById(R.id.cycleCount)
        phaseLabelText = findViewById(R.id.phaseLabel)
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupGestureDetection() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                togglePause()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                openSettings()
            }
        })
    }

    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            currentTimer?.cancel()
        } else {
            startTimer(secondsRemaining)
        }
        setKeepScreenOn(!isPaused)
    }

    private fun setKeepScreenOn(keepOn: Boolean) {
        if (keepOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
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
        currentTimer?.cancel()
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun startCurrentPhase(notify: Boolean = false) {
        val phase = phaseManager.getCurrentPhase()
        updateUI(phase)
        if (notify) {
            notifyPhaseChange(phase)
        }
        startTimer(getTimerDuration(phase))
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

    private fun startTimer(durationSeconds: Int) {
        currentTimer?.cancel()
        currentTimer = createTimer(durationSeconds).start()
        setKeepScreenOn(true)
    }

    private fun createTimer(durationSeconds: Int): CountDownTimer {
        return object : CountDownTimer(
            durationSeconds * MILLIS_PER_SECOND,
            MILLIS_PER_SECOND
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining =
                    (millisUntilFinished / MILLIS_PER_SECOND).toInt() + TIMER_DISPLAY_OFFSET
                timerText.text = formatNumber(remaining)
                this@MainActivity.secondsRemaining = remaining
            }

            override fun onFinish() {
                this@MainActivity.secondsRemaining = 0
                phaseManager.advanceToNextPhase()
                startCurrentPhase(notify = true)
            }
        }
    }

    private fun formatNumber(number: Int): String {
        return NumberFormat.getIntegerInstance(resources.configuration.locales[0]).format(number)
    }

    private fun updateUI(phase: PhaseState) {
        mainLayout.setBackgroundColor(phase.color.toColorInt())
        phaseLabelText.text = getPhaseLabel(phase)
        timerText.text = formatNumber(phase.durationSeconds)
        cycleCountText.text = getString(R.string.cycle_count, phaseManager.cycleCount + 1)
    }

    private fun getPhaseLabel(phase: PhaseState): String {
        return if (phase.isGo) getString(R.string.phase_go) else getString(R.string.phase_stop)
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
            setKeepScreenOn(true)
        }
    }

    override fun onPause() {
        super.onPause()
        saveCurrentState()
        setKeepScreenOn(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentTimer?.cancel()
    }
}
