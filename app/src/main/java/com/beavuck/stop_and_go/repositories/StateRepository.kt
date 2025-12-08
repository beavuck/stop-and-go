package com.beavuck.stop_and_go.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.beavuck.stop_and_go.model.AppState
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_IS_GO
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_IS_PAUSED
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_STOP_DURATION
import com.beavuck.stop_and_go.model.timer.TimerConstants.INITIAL_CYCLE_COUNT

class StateRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveState(state: AppState) {
        sharedPreferences.edit().apply {
            putInt(KEY_CYCLE_COUNT, state.cycleCount)
            putBoolean(KEY_IS_GO, state.isGo)
            putInt(KEY_CURRENT_GO_DURATION, state.currentGoDuration)
            putInt(KEY_CURRENT_STOP_DURATION, state.currentStopDuration)
            putInt(KEY_SECONDS_REMAINING, state.secondsRemaining)
            putInt(KEY_BASE_GO_DURATION, state.baseGoDuration)
            putInt(KEY_BASE_STOP_DURATION, state.baseStopDuration)
            putBoolean(KEY_IS_PAUSED, state.isPaused)
            apply()
        }
    }

    fun loadState(): AppState? {
        if (!sharedPreferences.contains(KEY_CYCLE_COUNT)) {
            return null
        }

        return AppState(
            cycleCount = sharedPreferences.getInt(KEY_CYCLE_COUNT, INITIAL_CYCLE_COUNT),
            isGo = sharedPreferences.getBoolean(KEY_IS_GO, DEFAULT_IS_GO),
            currentGoDuration = sharedPreferences.getInt(KEY_CURRENT_GO_DURATION, DEFAULT_GO_DURATION),
            currentStopDuration = sharedPreferences.getInt(KEY_CURRENT_STOP_DURATION, DEFAULT_STOP_DURATION),
            secondsRemaining = sharedPreferences.getInt(KEY_SECONDS_REMAINING, DEFAULT_GO_DURATION),
            baseGoDuration = sharedPreferences.getInt(KEY_BASE_GO_DURATION, DEFAULT_GO_DURATION),
            baseStopDuration = sharedPreferences.getInt(KEY_BASE_STOP_DURATION, DEFAULT_STOP_DURATION),
            isPaused = sharedPreferences.getBoolean(KEY_IS_PAUSED, DEFAULT_IS_PAUSED)
        )
    }

    fun clearState() {
        sharedPreferences.edit { clear() }
        setResetPending()
    }

    fun setResetPending(pending: Boolean = true) {
        sharedPreferences.edit {
            putBoolean(KEY_RESET_PENDING, pending)
        }
    }

    fun isResetPending(): Boolean {
        return sharedPreferences.getBoolean(KEY_RESET_PENDING, false)
    }

    @Suppress("unused", "RedundantSuppression")
    companion object {
        private const val PREFS_NAME = "stop_and_go_state"
        private const val KEY_CYCLE_COUNT = "cycle_count"
        private const val KEY_IS_GO = "is_go"
        private const val KEY_CURRENT_GO_DURATION = "current_go_duration"
        private const val KEY_CURRENT_STOP_DURATION = "current_stop_duration"
        private const val KEY_SECONDS_REMAINING = "seconds_remaining"
        private const val KEY_BASE_GO_DURATION = "base_go_duration"
        private const val KEY_BASE_STOP_DURATION = "base_stop_duration"
        private const val KEY_IS_PAUSED = "is_paused"
        private const val KEY_RESET_PENDING = "reset_pending"
    }
}
