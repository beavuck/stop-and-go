package com.beavuck.stop_and_go.timer

import android.os.CountDownTimer
import com.beavuck.stop_and_go.model.TimerConstants.MILLIS_PER_SECOND
import com.beavuck.stop_and_go.model.TimerConstants.TIMER_DISPLAY_OFFSET

class TimerController(
    private val onTick: (secondsRemaining: Int) -> Unit,
    private val onFinish: () -> Unit
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
                val remaining =
                    (millisUntilFinished / MILLIS_PER_SECOND).toInt() + TIMER_DISPLAY_OFFSET
                onTick(remaining)
            }

            override fun onFinish() {
                onFinish()
            }
        }
    }
}
