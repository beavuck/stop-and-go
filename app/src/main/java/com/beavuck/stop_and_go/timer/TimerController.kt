package com.beavuck.stop_and_go.timer

import android.os.CountDownTimer
import com.beavuck.stop_and_go.model.TimerConstants.MILLIS_PER_SECOND
import com.beavuck.stop_and_go.model.TimerConstants.TIMER_DISPLAY_OFFSET

class TimerController(
    private val onTickCallback: (secondsRemaining: Int) -> Unit,
    private val onFinishCallback: () -> Unit
) {
    private var currentTimer: CountDownTimer? = null

    fun start(durationSeconds: Int) {
        cancel()
        currentTimer = createTimer(durationSeconds).start()
    }

    fun cancel() {
        currentTimer?.cancel()
        currentTimer = null
    }

    private fun createTimer(durationSeconds: Int): CountDownTimer {
        return object : CountDownTimer(
            durationSeconds * MILLIS_PER_SECOND,
            MILLIS_PER_SECOND
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = toSeconds(millisUntilFinished).toInt() + TIMER_DISPLAY_OFFSET
                onTickCallback(remaining)
            }

            override fun onFinish() {
                onFinishCallback()
            }
        }
    }

    private fun toSeconds(millisUntilFinished: Long): Long = (millisUntilFinished / MILLIS_PER_SECOND)
}
