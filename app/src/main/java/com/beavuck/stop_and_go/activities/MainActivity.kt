package com.beavuck.stop_and_go.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beavuck.stop_and_go.model.PhaseManager
import com.beavuck.stop_and_go.model.PhaseState
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.TimerConfig

class MainActivity : AppCompatActivity() {
    private lateinit var phaseManager: PhaseManager
    private var currentTimer: CountDownTimer? = null
    private lateinit var timerText: TextView
    private lateinit var cycleCountText: TextView
    private lateinit var phaseLabelText: TextView
    private lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main)
        timerText = findViewById(R.id.timerText)
        cycleCountText = findViewById(R.id.cycleCount)
        phaseLabelText = findViewById(R.id.phaseLabel)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        phaseManager = PhaseManager(TimerConfig().validate())

        startCurrentPhase()
    }

    private fun startCurrentPhase() {
        val phase = phaseManager.getCurrentPhase()

        updateUI(phase)

        currentTimer?.cancel()

        currentTimer = object : CountDownTimer(
            phase.durationSeconds * 1000L,
            1000L
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt() + 1
                timerText.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                phaseManager.advanceToNextPhase()
                startCurrentPhase()
            }
        }.start()
    }

    private fun updateUI(phase: PhaseState) {
        mainLayout.setBackgroundColor(phase.color.toColorInt())

        phaseLabelText.text = if (phase.isGo) {
            getString(R.string.phase_go)
        } else {
            getString(R.string.phase_stop)
        }

        timerText.text = phase.durationSeconds.toString()

        cycleCountText.text = getString(
            R.string.cycle_count,
            phaseManager.cycleCount + 1 // Count should start from 1 for humans, though it starts from 0 internally
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        currentTimer?.cancel()
    }
}
