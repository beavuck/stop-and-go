package com.beavuck.stop_and_go.ui.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEBOUNCE_DELAY
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_COLOR
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEFAULT_GO_DURATION
import com.beavuck.stop_and_go.utils.TimeFormat
import com.beavuck.stop_and_go.utils.splitTime

enum class GestureType {
    TAP, LONG_PRESS, MULTI_TAP
}

@Composable
fun GestureDemoStep(
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val currentGesture = rememberSaveable { mutableIntStateOf(0) }
    val gestureCompleted = rememberSaveable { mutableStateOf(false) }

    val gestures = listOf(
        GestureType.TAP,
        GestureType.MULTI_TAP,
        GestureType.LONG_PRESS
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 48.dp, start = 32.dp, end = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.tutorial_gestures_title),
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.tutorial_progress, currentGesture.intValue + 1, 3),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = when (gestures[currentGesture.intValue]) {
                GestureType.TAP -> stringResource(R.string.tutorial_tap_instruction)
                GestureType.LONG_PRESS -> stringResource(R.string.tutorial_long_instruction)
                GestureType.MULTI_TAP -> stringResource(R.string.tutorial_multi_instruction)
            },
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        DemoArea(
            gestureType = gestures[currentGesture.intValue],
            onGestureDetected = {
                gestureCompleted.value = true
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (gestureCompleted.value) {
            Text(
                text = when (gestures[currentGesture.intValue]) {
                    GestureType.TAP -> stringResource(R.string.tutorial_tap_success)
                    GestureType.LONG_PRESS -> stringResource(R.string.tutorial_long_success)
                    GestureType.MULTI_TAP -> stringResource(R.string.tutorial_multi_success)
                },
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TutorialBottomButtons(
            onSkip = onSkip,
            onNext = {
                if (currentGesture.intValue < 2) {
                    currentGesture.intValue++
                    gestureCompleted.value = false
                } else {
                    onComplete()
                }
            }
        )
    }
}

@Composable
private fun DemoArea(
    gestureType: GestureType,
    onGestureDetected: () -> Unit
) {
    val showPauseOverlay = rememberSaveable { mutableStateOf(false) }
    val showSettingsOverlay = rememberSaveable { mutableStateOf(false) }
    val timerValue = rememberSaveable { mutableIntStateOf(8) }
    val tapCount = remember { mutableIntStateOf(0) }
    val lastTapTime = remember { mutableLongStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(DEFAULT_GO_COLOR.toColorInt()))
            .pointerInput(gestureType) {
                detectTapGestures(
                    onTap = {
                        when (gestureType) {
                            GestureType.TAP -> {
                                showPauseOverlay.value = !showPauseOverlay.value
                                onGestureDetected()
                            }

                            GestureType.MULTI_TAP -> {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastTapTime.longValue < DEBOUNCE_DELAY) {
                                    tapCount.intValue++
                                    if (tapCount.intValue >= 2) {
                                        timerValue.intValue = DEFAULT_GO_DURATION
                                        onGestureDetected()
                                        tapCount.intValue = 0
                                    }
                                } else {
                                    tapCount.intValue = 0
                                }
                                lastTapTime.longValue = currentTime
                            }

                            else -> { /* no-op */
                            }
                        }
                    },
                    onLongPress = {
                        if (gestureType == GestureType.LONG_PRESS) {
                            showSettingsOverlay.value = true
                            onGestureDetected()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val timeComponents = splitTime(timerValue.intValue)
        val formattedTime = when (timeComponents.format) {
            TimeFormat.S -> stringResource(R.string.timer_format_s, timeComponents.seconds)
            TimeFormat.MS -> stringResource(
                R.string.timer_format_ms,
                timeComponents.minutes,
                timeComponents.seconds
            )

            TimeFormat.HMS -> stringResource(
                R.string.timer_format_hms,
                timeComponents.hours,
                timeComponents.minutes,
                timeComponents.seconds
            )
        }
        Text(
            text = formattedTime,
            fontSize = 120.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(if (showSettingsOverlay.value) 0.3f else 1f)
        )

        if (showPauseOverlay.value && gestureType == GestureType.TAP) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.pause),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .alpha(0.7f)
                )
            }
        }

        if (showSettingsOverlay.value && gestureType == GestureType.LONG_PRESS) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2196F3).copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.gear),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .alpha(0.8f)
                )
            }
        }
    }
}
