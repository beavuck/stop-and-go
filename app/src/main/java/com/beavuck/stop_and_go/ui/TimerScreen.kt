package com.beavuck.stop_and_go.ui

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.beavuck.stop_and_go.model.PhaseManager
import com.beavuck.stop_and_go.model.timer.TimerController
import com.beavuck.stop_and_go.notifications.PhaseNotificationManager
import com.beavuck.stop_and_go.repositories.StateRepository
import java.util.Locale

@Composable
fun TimerScreen(
    phaseManager: PhaseManager,
    stateRepository: StateRepository,
    notificationManager: PhaseNotificationManager,
    onNavigateToSettings: () -> Unit,
    onKeepScreenOnChange: (Boolean) -> Unit,
) {
    val locale = Locale.getDefault()

    var phase by remember { mutableStateOf(phaseManager.getCurrentPhase()) }
    var cycleCount by remember { mutableIntStateOf(phaseManager.cycleCount) }
    var secondsRemaining by remember { mutableIntStateOf(phase.durationSeconds) }
    var isPaused by remember { mutableStateOf(false) }

    var startNextPhase: (() -> Unit)? = null

    val timerController = remember {
        TimerController(
            onTickCallback = { remaining ->
                secondsRemaining = remaining
            },
            onFinishCallback = {
                phaseManager.advanceToNextPhase()
                cycleCount = phaseManager.cycleCount
                phase = phaseManager.getCurrentPhase()
                secondsRemaining = phase.durationSeconds

                if (phase.isGo) {
                    notificationManager.notifyGoPhase()
                } else {
                    notificationManager.notifyStopPhase()
                }

                startNextPhase?.invoke()
            }
        )
    }

    startNextPhase = { timerController.start(phase.durationSeconds) }

    DisposableEffect(Unit) {
        val savedState = stateRepository.loadState()
        if (savedState != null) {
            phaseManager.restoreState(savedState)
            cycleCount = phaseManager.cycleCount
            phase = phaseManager.getCurrentPhase()
            secondsRemaining = savedState.secondsRemaining
        }

        val startDuration = if (secondsRemaining > 0) {
            val duration = secondsRemaining
            secondsRemaining = 0
            duration
        } else {
            phase.durationSeconds
        }

        timerController.start(startDuration)
        onKeepScreenOnChange(true)

        onDispose {
            timerController.cancel()
            if (!stateRepository.isResetPending()) {
                val state = phaseManager.getState().copy(secondsRemaining = secondsRemaining)
                stateRepository.saveState(state)
            } else {
                stateRepository.setResetPending(false) // now is the time to clear that flag -- any earlier and we might re-save the state before using it to reset
            }
            onKeepScreenOnChange(false)
        }
    }

    fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            timerController.cancel()
            onKeepScreenOnChange(false)
        } else {
            timerController.start(secondsRemaining)
            onKeepScreenOnChange(true)
        }
    }

    fun resetTimer() {
        timerController.cancel()
        phaseManager.reset()
        stateRepository.clearState()
        cycleCount = phaseManager.cycleCount
        phase = phaseManager.getCurrentPhase()
        secondsRemaining = phase.durationSeconds
        isPaused = false
        timerController.start(phase.durationSeconds)
        onKeepScreenOnChange(true)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var isInitialized by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (!isInitialized) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    isInitialized = true
                }
                return@LifecycleEventObserver
            }

            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (!isPaused) {
                        timerController.cancel()
                        onKeepScreenOnChange(false)
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (!isPaused) {
                        timerController.start(secondsRemaining)
                        onKeepScreenOnChange(true)
                    }
                }

                else -> { /* no-op */
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    TimerDisplay(
        phase = phase,
        secondsRemaining = secondsRemaining,
        cycleCount = cycleCount,
        locale = locale,
        isPaused = isPaused,
        goLabel = phaseManager.getGoLabel(),
        stopLabel = phaseManager.getStopLabel(),
        onTap = ::togglePause,
        onLongPress = onNavigateToSettings,
        onTripleTap = ::resetTimer,
    )
}
