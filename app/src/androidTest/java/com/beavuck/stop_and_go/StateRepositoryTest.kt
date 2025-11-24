package com.beavuck.stop_and_go

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.model.AppState
import com.beavuck.stop_and_go.repositories.StateRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StateRepositoryTest {
    private lateinit var context: Context
    private lateinit var repository: StateRepository
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = context.getSharedPreferences("stop_and_go_state", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
        repository = StateRepository(context)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun loadState_withNoSavedData_returnsNull() {
        val state = repository.loadState()

        assertNull(state)
    }

    @Test
    fun saveState_persistsAllValues() {
        val state = AppState(
            cycleCount = 5,
            isGo = false,
            currentGoDuration = 72,
            currentStopDuration = 20,
            secondsRemaining = 15,
            baseGoDuration = 60,
            baseStopDuration = 15
        )

        repository.saveState(state)
        val loadedState = repository.loadState()

        assertEquals(state, loadedState)
    }

    @Test
    fun saveState_overwritesPreviousState() {
        val state1 = AppState(
            cycleCount = 1,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 15,
            secondsRemaining = 30
        )
        repository.saveState(state1)

        val state2 = AppState(
            cycleCount = 3,
            isGo = false,
            currentGoDuration = 90,
            currentStopDuration = 25,
            secondsRemaining = 10
        )
        repository.saveState(state2)

        val loadedState = repository.loadState()

        assertEquals(state2, loadedState)
    }

    @Test
    fun clearState_removesPersistedState() {
        val state = AppState(
            cycleCount = 2,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 15,
            secondsRemaining = 45
        )
        repository.saveState(state)

        repository.clearState()
        val loadedState = repository.loadState()

        assertNull(loadedState)
    }

    @Test
    fun saveState_withEdgeCaseValues_persists() {
        val state = AppState(
            cycleCount = 0,
            isGo = true,
            currentGoDuration = 1,
            currentStopDuration = 1,
            secondsRemaining = 1
        )

        repository.saveState(state)
        val loadedState = repository.loadState()

        assertEquals(state, loadedState)
    }

    @Test
    fun saveState_withStopPhase_persistsCorrectly() {
        val state = AppState(
            cycleCount = 5,
            isGo = false,
            currentGoDuration = 120,
            currentStopDuration = 30,
            secondsRemaining = 15,
            baseGoDuration = 100,
            baseStopDuration = 25
        )

        repository.saveState(state)
        val loadedState = repository.loadState()

        assertEquals(state, loadedState)
        assertEquals(false, loadedState?.isGo)
    }

    @Test
    fun saveState_withHighCycleCount_persists() {
        val state = AppState(
            cycleCount = 1000,
            isGo = true,
            currentGoDuration = 3600,
            currentStopDuration = 3600,
            secondsRemaining = 3600,
            baseGoDuration = 60,
            baseStopDuration = 15
        )

        repository.saveState(state)
        val loadedState = repository.loadState()

        assertEquals(state, loadedState)
        assertEquals(1000, loadedState?.cycleCount)
    }

    @Test
    fun clearState_afterMultipleSaves_removesAll() {
        val state1 = AppState(
            cycleCount = 1,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 15,
            secondsRemaining = 30
        )
        repository.saveState(state1)

        val state2 = AppState(
            cycleCount = 2,
            isGo = false,
            currentGoDuration = 66,
            currentStopDuration = 16,
            secondsRemaining = 10
        )
        repository.saveState(state2)

        repository.clearState()
        val loadedState = repository.loadState()

        assertNull(loadedState)
    }

    @Test
    fun saveState_afterClear_persistsNewState() {
        val state1 = AppState(
            cycleCount = 5,
            isGo = false,
            currentGoDuration = 100,
            currentStopDuration = 50,
            secondsRemaining = 25
        )
        repository.saveState(state1)
        repository.clearState()

        val state2 = AppState(
            cycleCount = 0,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 15,
            secondsRemaining = 60
        )
        repository.saveState(state2)
        val loadedState = repository.loadState()

        assertEquals(state2, loadedState)
    }
}
