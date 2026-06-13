package com.beavuck.stop_and_go.ui.timer

import android.annotation.SuppressLint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.phase.PhaseManager
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_IS_PAUSED
import com.beavuck.stop_and_go.model.timer.TimerController
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.sounds.PhaseSoundPlayer
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    phaseManager: PhaseManager,
    stateRepository: StateRepository,
    soundPlayer: PhaseSoundPlayer,
    soundEnabled: Boolean,
    onNavigateToSettings: () -> Unit,
    onKeepScreenOnChange: (Boolean) -> Unit,
) {
    val phase = remember { mutableStateOf(phaseManager.getCurrentPhase()) }
    val cycleCount = remember { mutableIntStateOf(phaseManager.cycleCount) }
    val secondsRemaining = remember { mutableIntStateOf(phase.value.durationSeconds) }
    val isPaused = remember { mutableStateOf(DEFAULT_IS_PAUSED) }

    var startNextPhase: (() -> Unit)? = null

    val timerController = remember {
        TimerController(
            onTickCallback = { remaining ->
                secondsRemaining.intValue = remaining
            },
            onFinishCallback = {
                phaseManager.advanceToNextPhase()
                cycleCount.intValue = phaseManager.cycleCount
                phase.value = phaseManager.getCurrentPhase()
                secondsRemaining.intValue = phase.value.durationSeconds

                if (soundEnabled) {
                    if (phase.value.isGo) soundPlayer.playGoSound() else soundPlayer.playStopSound()
                }

                startNextPhase?.invoke()
            }
        )
    }

    startNextPhase = { timerController.start(phase.value.durationSeconds) }

    DisposableEffect(Unit) {
        val savedState = stateRepository.loadState()
        if (savedState != null) {
            phaseManager.restoreState(savedState)
            cycleCount.intValue = phaseManager.cycleCount
            phase.value = phaseManager.getCurrentPhase()
            secondsRemaining.intValue = savedState.secondsRemaining
            isPaused.value = savedState.isPaused
        }

        onDispose {
            timerController.pause()
            if (!stateRepository.isResetPending()) {
                val state = phaseManager.getState(secondsRemaining.intValue, isPaused.value)
                stateRepository.saveState(state)
            } else {
                stateRepository.setResetPending(false) // now is the time to clear that flag -- any earlier, and we might re-save the state before using it to reset
            }
            onKeepScreenOnChange(false)
        }
    }

    fun skipToNextPhase() {
        timerController.pause()
        phaseManager.advanceToNextPhase()
        cycleCount.intValue = phaseManager.cycleCount
        phase.value = phaseManager.getCurrentPhase()
        secondsRemaining.intValue = phase.value.durationSeconds
        if (!isPaused.value) {
            timerController.start(phase.value.durationSeconds)
        }
    }

    fun togglePause() {
        isPaused.value = !isPaused.value
        if (isPaused.value) {
            timerController.pause()
            onKeepScreenOnChange(false)
        } else {
            timerController.start(secondsRemaining.intValue)
            onKeepScreenOnChange(true)
        }
    }

    fun resetTimer() {
        timerController.pause()
        phaseManager.reset()
        stateRepository.clearState()
        cycleCount.intValue = phaseManager.cycleCount
        phase.value = phaseManager.getCurrentPhase()
        secondsRemaining.intValue = phase.value.durationSeconds
        isPaused.value = false
        timerController.start(phase.value.durationSeconds)
        onKeepScreenOnChange(true)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (!isPaused.value) {
                        timerController.pause()
                        onKeepScreenOnChange(false)
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (!isPaused.value) {
                        timerController.start(secondsRemaining.intValue)
                        onKeepScreenOnChange(true)
                    }
                }

                else -> {
                    /* no-op */
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val backgroundColor = Color(ColorUtils.parseColorSafely(phase.value.color))
    val iconColor = Color(ColorUtils.getContrastingTextColor(backgroundColor.toArgb()))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = iconColor,
                    actionIconContentColor = iconColor
                ),
                actions = {
                    IconButton(
                        onClick = ::skipToNextPhase,
                        modifier = Modifier.testTag("nextPhaseButton")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.next),
                            contentDescription = stringResource(R.string.next_phase)
                        )
                    }
                    IconButton(
                        onClick = ::resetTimer,
                        modifier = Modifier.testTag("resetTimerButton")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_rewind),
                            contentDescription = stringResource(R.string.reset_timer)
                        )
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settingsButton")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.gear),
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) {
        TimerDisplay(
            phase = phase.value,
            secondsRemaining = secondsRemaining.intValue,
            cycleCount = cycleCount.intValue,
            isPaused = isPaused.value,
            goLabel = phaseManager.getGoLabel(),
            stopLabel = phaseManager.getStopLabel(),
            onTap = ::togglePause,
            onLongPress = onNavigateToSettings,
            onTripleTap = ::resetTimer,
            onSwipeUp = ::skipToNextPhase
        )
    }
}
