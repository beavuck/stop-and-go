package com.beavuck.stop_and_go.sounds

import android.media.AudioManager
import android.media.ToneGenerator

class PhaseSoundPlayer {
    private val toneGenerator = try {
        ToneGenerator(AudioManager.STREAM_ALARM, TONE_VOLUME_PERCENT)
    } catch (_: RuntimeException) {
        null
    }

    fun playGoSound() = toneGenerator?.startTone(ToneGenerator.TONE_SUP_INTERCEPT, TONE_DURATION_MS)
    fun playStopSound() =
        toneGenerator?.startTone(ToneGenerator.TONE_SUP_RADIO_ACK, TONE_DURATION_MS)

    fun release() = toneGenerator?.release()

    companion object {
        private const val TONE_VOLUME_PERCENT = 100
        private const val TONE_DURATION_MS = 500
    }
}
