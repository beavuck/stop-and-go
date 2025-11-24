package com.beavuck.stop_and_go.repositories

import android.content.Context
import android.content.SharedPreferences
import com.beavuck.stop_and_go.model.AppState
import androidx.core.content.edit

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
            apply()
        }
    }

    fun loadState(): AppState? {
        if (!sharedPreferences.contains(KEY_CYCLE_COUNT)) {
            return null
        }

        return AppState(
            cycleCount = sharedPreferences.getInt(KEY_CYCLE_COUNT, 0),
            isGo = sharedPreferences.getBoolean(KEY_IS_GO, true),
            currentGoDuration = sharedPreferences.getInt(KEY_CURRENT_GO_DURATION, 0),
            currentStopDuration = sharedPreferences.getInt(KEY_CURRENT_STOP_DURATION, 0),
            secondsRemaining = sharedPreferences.getInt(KEY_SECONDS_REMAINING, 0),
            baseGoDuration = sharedPreferences.getInt(KEY_BASE_GO_DURATION, 0),
            baseStopDuration = sharedPreferences.getInt(KEY_BASE_STOP_DURATION, 0)
        )
    }

    fun clearState() {
        sharedPreferences.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "stop_and_go_state"
        private const val KEY_CYCLE_COUNT = "cycle_count"
        private const val KEY_IS_GO = "is_go"
        private const val KEY_CURRENT_GO_DURATION = "current_go_duration"
        private const val KEY_CURRENT_STOP_DURATION = "current_stop_duration"
        private const val KEY_SECONDS_REMAINING = "seconds_remaining"
        private const val KEY_BASE_GO_DURATION = "base_go_duration"
        private const val KEY_BASE_STOP_DURATION = "base_stop_duration"
    }
}
