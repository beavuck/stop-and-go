package com.beavuck.stop_and_go

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.repositories.TutorialRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TutorialRepositoryTest {
    private lateinit var context: Context
    private lateinit var repository: TutorialRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = TutorialRepository(context)
        clearPreferences()
    }

    @After
    fun tearDown() {
        clearPreferences()
    }

    private fun clearPreferences() {
        context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun shouldShowTutorial_whenNeverCompleted_returnsTrue() {
        assertTrue(repository.shouldShowTutorial())
    }

    @Test
    fun shouldShowTutorial_whenCompletedSameVersion_returnsFalse() {
        repository.markTutorialComplete()

        assertFalse(repository.shouldShowTutorial())
    }

    @Test
    fun shouldShowTutorial_whenCompletedOldVersion_returnsTrue() {
        val prefs = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("tutorial_completed", true)
            putInt("tutorial_version", 0)
            apply()
        }

        assertTrue(repository.shouldShowTutorial())
    }

    @Test
    fun markTutorialComplete_savesCompletionFlag() {
        repository.markTutorialComplete()

        val prefs = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
        assertTrue(prefs.getBoolean("tutorial_completed", false))
    }

    @Test
    fun markTutorialComplete_savesCurrentVersion() {
        repository.markTutorialComplete()

        val prefs = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
        assertEquals(
            TutorialRepository.CURRENT_TUTORIAL_VERSION,
            prefs.getInt("tutorial_version", 0)
        )
    }
}
