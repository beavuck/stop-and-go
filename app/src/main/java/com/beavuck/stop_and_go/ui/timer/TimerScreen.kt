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
import com.beavuck.stop_and_go.notifications.PhaseNotificationManager
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    phaseManager: PhaseManager,
    stateRepository: StateRepository,
    notificationManager: PhaseNotificationManager,
    onNavigateToSettings: () -> Unit,
    onKeepScreenOnChange: (Boolean) -> Unit,
) {
    var phase by remember { mutableStateOf(phaseManager.getCurrentPhase()) }
    var cycleCount by remember { mutableIntStateOf(phaseManager.cycleCount) }
    var secondsRemaining by remember { mutableIntStateOf(phase.durationSeconds) }
    var isPaused by remember { mutableStateOf(DEFAULT_IS_PAUSED) }

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
                    notificationManager.notifyGoPhase(phaseManager.getGoLabel())
                } else {
                    notificationManager.notifyStopPhase(phaseManager.getStopLabel())
                }

                startNextPhase?.invoke()
            }
        )
    }

    // actually used despite IDE warning -- see above
    startNextPhase = { timerController.start(phase.durationSeconds) }

    DisposableEffect(Unit) {
        val savedState = stateRepository.loadState()
        if (savedState != null) {
            phaseManager.restoreState(savedState)
            cycleCount = phaseManager.cycleCount
            phase = phaseManager.getCurrentPhase()
            secondsRemaining = savedState.secondsRemaining
            isPaused = savedState.isPaused
        }

        onDispose {
            timerController.pause()
            if (!stateRepository.isResetPending()) {
                val state = phaseManager.getState(secondsRemaining, isPaused)
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
            timerController.pause()
            onKeepScreenOnChange(false)
        } else {
            timerController.start(secondsRemaining)
            onKeepScreenOnChange(true)
        }
    }

    fun resetTimer() {
        timerController.pause()
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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (!isPaused) {
                        timerController.pause()
                        onKeepScreenOnChange(false)
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (!isPaused) {
                        timerController.start(secondsRemaining)
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

    val backgroundColor = Color(ColorUtils.parseColorSafely(phase.color))
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
                        onClick = ::resetTimer,
                        modifier = Modifier.testTag("resetTimerButton")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.reset),
                            contentDescription = stringResource(R.string.reset_timer)
                        )
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settingsButton")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.settings),
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) {
        TimerDisplay(
            phase = phase,
            secondsRemaining = secondsRemaining,
            cycleCount = cycleCount,
            isPaused = isPaused,
            goLabel = phaseManager.getGoLabel(),
            stopLabel = phaseManager.getStopLabel(),
            onTap = ::togglePause,
            onLongPress = onNavigateToSettings,
            onTripleTap = ::resetTimer
        )
    }
}
