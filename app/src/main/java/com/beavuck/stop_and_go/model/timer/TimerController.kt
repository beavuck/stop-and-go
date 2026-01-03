package com.beavuck.stop_and_go.model.timer

import android.os.CountDownTimer

private const val MILLIS_PER_SECOND = 1000L
private const val TIMER_DISPLAY_OFFSET = 1

class TimerController(
    private val onTickCallback: (secondsRemaining: Int) -> Unit,
    private val onFinishCallback: () -> Unit
) {

    private var currentTimer: CountDownTimer? = null

    fun start(durationSeconds: Int) {
        pause()
        currentTimer = createTimer(durationSeconds).start()
    }

    fun pause() {
        currentTimer?.cancel()
        currentTimer = null
    }

    private fun createTimer(durationSeconds: Int): CountDownTimer {
        return object : CountDownTimer(
            toMillis(durationSeconds),
            toMillis(1)
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = toSeconds(millisUntilFinished).toInt() + TIMER_DISPLAY_OFFSET
                onTickCallback(remaining.coerceAtMost(durationSeconds))
            }

            override fun onFinish() {
                onFinishCallback()
            }
        }
    }

    private fun toSeconds(millisUntilFinished: Long): Long =
        (millisUntilFinished / MILLIS_PER_SECOND)

    private fun toMillis(seconds: Int): Long =
        seconds * MILLIS_PER_SECOND
}
