package com.beavuck.stop_and_go.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.phase.PhaseState
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils


@Composable
fun TimerDisplay(
    phase: PhaseState,
    secondsRemaining: Int,
    cycleCount: Int,
    isPaused: Boolean,
    goLabel: String,
    stopLabel: String,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onTripleTap: () -> Unit = {},
) {
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    val backgroundColor = Color(ColorUtils.parseColorSafely(phase.color))
    val textColor = Color(ColorUtils.getContrastingTextColor(backgroundColor.toArgb()))

    val defaultGoLabel = stringResource(R.string.phase_go)
    val defaultStopLabel = stringResource(R.string.phase_stop)
    val phaseLabel = if (phase.isGo) {
        goLabel.ifEmpty { defaultGoLabel }
    } else {
        stopLabel.ifEmpty { defaultStopLabel }
    }
    val formattedTime = stringResource(R.string.timer_seconds, secondsRemaining)
    val cycleText = stringResource(R.string.cycle_count, cycleCount + 1)
    val screenDescription = stringResource(R.string.timer_screen)

    val pauseStateDescription = if (isPaused) {
        stringResource(R.string.timer_paused)
    } else {
        stringResource(R.string.timer_resumed)
    }

    val phaseChangeDescription = pluralStringResource(
        R.plurals.phase_changed,
        secondsRemaining,
        phaseLabel,
        secondsRemaining
    )

    val pauseLabel = stringResource(if (isPaused) R.string.resume else R.string.pause)
    val settingsLabel = stringResource(R.string.settings)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .semantics {
                contentDescription = screenDescription
                stateDescription = if (isPaused) {
                    pauseStateDescription
                } else {
                    phaseChangeDescription
                }
                onClick(label = pauseLabel) {
                    onTap()
                    true
                }
                onLongClick(label = settingsLabel) {
                    onLongPress()
                    true
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTapTime < 500) {
                            tapCount++
                            if (tapCount >= 2) {
                                onTripleTap()
                                tapCount = 0
                            }
                        } else {
                            tapCount = 0
                            onTap()
                        }
                        lastTapTime = currentTime
                    },
                    onLongPress = { onLongPress() }
                )
            }
            .testTag("timerDisplay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = phaseLabel,
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                color = textColor,
                modifier = Modifier
                    .padding(all = 48.dp)
                    .alpha(0.8f)
                    .testTag("phaseLabel"),
            )

            Text(
                text = formattedTime,
                fontSize = 120.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                color = textColor,
                modifier = Modifier.testTag("timerText")
            )
        }

        Text(
            text = cycleText,
            fontSize = 24.sp,
            color = textColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 48.dp)
                .alpha(0.7f)
                .testTag("cycleCount")
        )

        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .testTag("pauseOverlay"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.pause),
                    contentDescription = stringResource(R.string.timer_paused),
                    tint = textColor,
                    modifier = Modifier
                        .size(64.dp)
                        .alpha(0.7f)
                        .testTag("pauseIcon")
                )
            }
        }
    }
}
