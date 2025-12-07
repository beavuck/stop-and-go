package com.beavuck.stop_and_go

import com.beavuck.stop_and_go.model.AppState
import org.junit.Assert.*
import org.junit.Test

class AppStateTest {

    @Test
    fun constructor_withAllParameters_createsCorrectState() {
        val state = AppState(
            cycleCount = 5,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 45,
            baseGoDuration = 60,
            baseStopDuration = 30
        )

        assertEquals(5, state.cycleCount)
        assertTrue(state.isGo)
        assertEquals(60, state.currentGoDuration)
        assertEquals(30, state.currentStopDuration)
        assertEquals(45, state.secondsRemaining)
        assertEquals(60, state.baseGoDuration)
        assertEquals(30, state.baseStopDuration)
    }

    @Test
    fun copy_withNoChanges_createsEqualState() {
        val original = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20,
            baseGoDuration = 50,
            baseStopDuration = 25
        )

        val copied = original.copy()

        assertEquals(original, copied)
    }

    @Test
    fun copy_withChangedCycleCount_updatesOnlyThatField() {
        val original = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )

        val modified = original.copy(cycleCount = 5)

        assertEquals(5, modified.cycleCount)
        assertEquals(original.isGo, modified.isGo)
        assertEquals(original.currentGoDuration, modified.currentGoDuration)
        assertEquals(original.currentStopDuration, modified.currentStopDuration)
        assertEquals(original.secondsRemaining, modified.secondsRemaining)
    }

    @Test
    fun copy_withChangedIsGo_updatesOnlyThatField() {
        val original = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )

        val modified = original.copy(isGo = false)

        assertFalse(modified.isGo)
        assertEquals(original.cycleCount, modified.cycleCount)
    }

    @Test
    fun equals_withSameValues_returnsTrue() {
        val state1 = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )
        val state2 = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )

        assertEquals(state1, state2)
    }

    @Test
    fun equals_withDifferentValues_returnsFalse() {
        val state1 = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )
        val state2 = AppState(
            cycleCount = 4,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )

        assertNotEquals(state1, state2)
    }

    @Test
    fun hashCode_withSameValues_returnsSameHash() {
        val state1 = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )
        val state2 = AppState(
            cycleCount = 3,
            isGo = true,
            currentGoDuration = 60,
            currentStopDuration = 30,
            secondsRemaining = 20
        )

        assertEquals(state1.hashCode(), state2.hashCode())
    }

    @Test
    fun constructor_withNoParameters_usesDefaultValues() {
        val state = AppState()

        assertEquals(0, state.cycleCount)
        assertTrue(state.isGo)
        assertEquals(60, state.currentGoDuration)
        assertEquals(15, state.currentStopDuration)
        assertEquals(60, state.secondsRemaining)
        assertEquals(60, state.baseGoDuration)
        assertEquals(15, state.baseStopDuration)
    }
}
