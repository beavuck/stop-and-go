package com.beavuck.stop_and_go.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.PhaseManager
import com.beavuck.stop_and_go.model.PhaseState
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository

class MainActivity : AppCompatActivity() {
    private lateinit var phaseManager: PhaseManager
    private lateinit var stateRepository: StateRepository
    private lateinit var gestureDetector: GestureDetector
    private var currentTimer: CountDownTimer? = null
    private var secondsRemaining: Int = 0
    private var isPaused: Boolean = false

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var timerText: TextView
    private lateinit var cycleCountText: TextView
    private lateinit var phaseLabelText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bindViews()
        setupEdgeToEdge()
        setupGestureDetection()

        stateRepository = StateRepository(this)
        initializePhaseManager()
        restoreSavedState()
        startCurrentPhase()
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
        if (isPaused) {
            isPaused = false
            startTimer(secondsRemaining)
            setKeepScreenOn(true)
        } else {
            isPaused = true
            currentTimer?.cancel()
            setKeepScreenOn(false)
        }
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

    private fun startCurrentPhase() {
        val phase = phaseManager.getCurrentPhase()
        updateUI(phase)
        startTimer(getTimerDuration(phase))
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
        return object : CountDownTimer(durationSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = (millisUntilFinished / 1000).toInt() + 1
                timerText.text = remaining.toString()
                this@MainActivity.secondsRemaining = remaining
            }

            override fun onFinish() {
                this@MainActivity.secondsRemaining = 0
                phaseManager.advanceToNextPhase()
                startCurrentPhase()
            }
        }
    }

    private fun updateUI(phase: PhaseState) {
        mainLayout.setBackgroundColor(phase.color.toColorInt())
        phaseLabelText.text = getPhaseLabel(phase)
        timerText.text = phase.durationSeconds.toString()
        cycleCountText.text = getString(R.string.cycle_count, phaseManager.cycleCount + 1)
    }

    private fun getPhaseLabel(phase: PhaseState): String {
        return if (phase.isGo) getString(R.string.phase_go) else getString(R.string.phase_stop)
    }

    private fun saveCurrentState() {
        val state = phaseManager.getState().copy(secondsRemaining = secondsRemaining)
        stateRepository.saveState(state)
    }

    override fun onResume() {
        super.onResume()
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
