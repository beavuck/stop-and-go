package com.beavuck.stop_and_go.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.phase.PhaseState
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEBOUNCE_DELAY
import com.beavuck.stop_and_go.utils.TimeFormat
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils
import com.beavuck.stop_and_go.utils.splitTime


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
    val tapCount = remember { mutableIntStateOf(0) }
    val lastTapTime = remember { mutableLongStateOf(0L) }
    val backgroundColor = Color(ColorUtils.parseColorSafely(phase.color))
    val textColor = Color(ColorUtils.getContrastingTextColor(backgroundColor.toArgb()))

    val defaultGoLabel = stringResource(R.string.phase_go)
    val defaultStopLabel = stringResource(R.string.phase_stop)
    val phaseLabel = if (phase.isGo) {
        goLabel.ifEmpty { defaultGoLabel }
    } else {
        stopLabel.ifEmpty { defaultStopLabel }
    }
    val timeComponents = splitTime(secondsRemaining)
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
                        if (currentTime - lastTapTime.longValue < DEBOUNCE_DELAY) {
                            tapCount.intValue++
                            if (tapCount.intValue >= 2) {
                                onTripleTap()
                                tapCount.intValue = 0
                            }
                        } else {
                            tapCount.intValue = 0
                            onTap()
                        }
                        lastTapTime.longValue = currentTime
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

            BasicText(
                text = formattedTime,
                autoSize = TextAutoSize.StepBased(minFontSize = 20.sp, maxFontSize = 120.sp),
                maxLines = 1,
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                ),
                color = { textColor },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("timerText")
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
