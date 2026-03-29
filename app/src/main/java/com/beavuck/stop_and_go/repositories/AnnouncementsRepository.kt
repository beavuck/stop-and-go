package com.beavuck.stop_and_go.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AnnouncementsRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // (2026-10): Remove this
    fun shouldShowSoundMigrationAnnouncement(): Boolean =
        !sharedPreferences.getBoolean(KEY_SOUND_MIGRATION_SHOWN, false)

    // (2026-10): Remove this
    fun markSoundMigrationAnnouncementShown() {
        sharedPreferences.edit { putBoolean(KEY_SOUND_MIGRATION_SHOWN, true) }
    }

    companion object {
        private const val PREFS_NAME = "announcements_prefs"

        // (2026-10): Remove this
        private const val KEY_SOUND_MIGRATION_SHOWN = "sound_migration_shown"
    }
}
