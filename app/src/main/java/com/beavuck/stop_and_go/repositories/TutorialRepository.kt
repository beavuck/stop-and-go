package com.beavuck.stop_and_go.repositories

import android.content.Context
import android.content.SharedPreferences

class TutorialRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun shouldShowTutorial(): Boolean {
        val completed = sharedPreferences.getBoolean(KEY_TUTORIAL_COMPLETED, false)
        val savedVersion = sharedPreferences.getInt(KEY_TUTORIAL_VERSION, 0)
        return !completed || savedVersion < CURRENT_TUTORIAL_VERSION
    }

    fun markTutorialComplete() {
        sharedPreferences.edit().apply {
            putBoolean(KEY_TUTORIAL_COMPLETED, true)
            putInt(KEY_TUTORIAL_VERSION, CURRENT_TUTORIAL_VERSION)
            apply()
        }
    }

    fun resetTutorialCompletion() {
        sharedPreferences.edit().apply {
            putBoolean(KEY_TUTORIAL_COMPLETED, false)
            putInt(KEY_TUTORIAL_VERSION, 0)
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "tutorial_prefs"
        private const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"
        private const val KEY_TUTORIAL_VERSION = "tutorial_version"
        const val CURRENT_TUTORIAL_VERSION = 1
    }
}
