package com.beavuck.stop_and_go

import com.beavuck.stop_and_go.model.PhaseManager
import com.beavuck.stop_and_go.model.TimerConfig
import com.beavuck.stop_and_go.model.TimerConstants.INITIAL_CYCLE_COUNT
import com.beavuck.stop_and_go.model.TimerConstants.MAX_DURATION_SECONDS
import com.beavuck.stop_and_go.model.TimerConstants.MIN_DURATION_SECONDS
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PhaseManagerTest {
    private lateinit var config: TimerConfig
    private lateinit var phaseManager: PhaseManager

    @Before
    fun setup() {
        config = TimerConfig()
        phaseManager = PhaseManager(config)
    }

    @Test
    fun initialPhase_isGo() {
        val phase = phaseManager.getCurrentPhase()

        assertTrue(phase.isGo)
        assertEquals(config.goColor, phase.color)
        assertEquals(config.goDuration, phase.durationSeconds)
    }

    @Test
    fun initialCycleCount_isZero() {
        assertEquals(INITIAL_CYCLE_COUNT, phaseManager.cycleCount)
    }

    @Test
    fun isGo_initiallyReturnsTrue() {
        assertTrue(phaseManager.isGo())
    }

    @Test
    fun advanceToNextPhase_fromGoToStop_switchesPhase() {
        phaseManager.advanceToNextPhase()

        val phase = phaseManager.getCurrentPhase()
        assertFalse(phase.isGo)
        assertEquals(config.stopColor, phase.color)
        assertEquals(config.stopDuration, phase.durationSeconds)
    }

    @Test
    fun advanceToNextPhase_fromGoToStop_doesNotIncrementCycle() {
        phaseManager.advanceToNextPhase()

        assertEquals(INITIAL_CYCLE_COUNT, phaseManager.cycleCount)
    }

    @Test
    fun advanceToNextPhase_fromStopToGo_switchesPhase() {
        phaseManager.advanceToNextPhase()
        phaseManager.advanceToNextPhase()

        val phase = phaseManager.getCurrentPhase()
        assertTrue(phase.isGo)
    }

    @Test
    fun advanceToNextPhase_fromStopToGo_incrementsCycle() {
        phaseManager.advanceToNextPhase()
        phaseManager.advanceToNextPhase()

        assertEquals(1, phaseManager.cycleCount)
    }

    @Test
    fun advanceToNextPhase_multipleCycles_incrementsCycleCount() {
        repeat(6) { phaseManager.advanceToNextPhase() }

        assertEquals(3, phaseManager.cycleCount)
    }

    @Test
    fun advanceToNextPhase_withGrowth_appliesGoDurationGrowth() {
        val growthConfig = TimerConfig(
            goDuration = 60,
            stopDuration = 15,
            goDurationGrowth = 1.1f,
            stopDurationGrowth = 1.0f
        )
        val manager = PhaseManager(growthConfig)

        manager.advanceToNextPhase()
        manager.advanceToNextPhase()

        val phase = manager.getCurrentPhase()
        assertEquals(66, phase.durationSeconds)
    }

    @Test
    fun advanceToNextPhase_withGrowth_appliesStopDurationGrowth() {
        val growthConfig = TimerConfig(
            goDuration = 60,
            stopDuration = 20,
            goDurationGrowth = 1.0f,
            stopDurationGrowth = 1.5f
        )
        val manager = PhaseManager(growthConfig)

        manager.advanceToNextPhase()
        manager.advanceToNextPhase()
        manager.advanceToNextPhase()

        val phase = manager.getCurrentPhase()
        assertEquals(30, phase.durationSeconds)
    }

    @Test
    fun advanceToNextPhase_withGrowth_appliesBothGrowthMultipliers() {
        val growthConfig = TimerConfig(
            goDuration = 100,
            stopDuration = 50,
            goDurationGrowth = 1.2f,
            stopDurationGrowth = 1.1f
        )
        val manager = PhaseManager(growthConfig)

        repeat(4) { manager.advanceToNextPhase() }

        val goPhase = manager.getCurrentPhase()
        assertEquals((100 * 1.2 * 1.2).toInt(), goPhase.durationSeconds)

        manager.advanceToNextPhase()
        val stopPhase = manager.getCurrentPhase()
        assertEquals((50 * 1.1 * 1.1).toInt(), stopPhase.durationSeconds)
    }

    @Test
    fun advanceToNextPhase_withGrowthExceedingMax_capsAtMaxDuration() {
        val growthConfig = TimerConfig(
            goDuration = MAX_DURATION_SECONDS - 10,
            stopDuration = 15,
            goDurationGrowth = 2.0f,
            stopDurationGrowth = 1.0f
        )
        val manager = PhaseManager(growthConfig)

        manager.advanceToNextPhase()
        manager.advanceToNextPhase()

        val phase = manager.getCurrentPhase()
        assertEquals(MAX_DURATION_SECONDS, phase.durationSeconds)
    }

    @Test
    fun advanceToNextPhase_withGrowthBelowMin_capsAtMinDuration() {
        val growthConfig = TimerConfig(
            goDuration = 10,
            stopDuration = 15,
            goDurationGrowth = 0.05f,
            stopDurationGrowth = 1.0f
        )
        val manager = PhaseManager(growthConfig)

        manager.advanceToNextPhase()
        manager.advanceToNextPhase()

        val phase = manager.getCurrentPhase()
        assertEquals(MIN_DURATION_SECONDS, phase.durationSeconds)
    }

    @Test
    fun reset_resetsToInitialGoDuration() {
        val growthConfig = TimerConfig(
            goDuration = 60,
            stopDuration = 15,
            goDurationGrowth = 1.5f,
            stopDurationGrowth = 1.0f
        )
        val manager = PhaseManager(growthConfig)

        manager.advanceToNextPhase()
        manager.advanceToNextPhase()
        manager.reset()

        val phase = manager.getCurrentPhase()
        assertEquals(60, phase.durationSeconds)
    }

    @Test
    fun reset_resetsCycleCount() {
        repeat(6) { phaseManager.advanceToNextPhase() }
        phaseManager.reset()

        assertEquals(INITIAL_CYCLE_COUNT, phaseManager.cycleCount)
    }

    @Test
    fun reset_resetsToGoPhase() {
        phaseManager.advanceToNextPhase()
        phaseManager.reset()

        assertTrue(phaseManager.isGo())
        val phase = phaseManager.getCurrentPhase()
        assertTrue(phase.isGo)
    }
}
