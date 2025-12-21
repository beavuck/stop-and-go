package com.beavuck.stop_and_go.ui.tutorial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.beavuck.stop_and_go.R

@Composable
fun TutorialBottomButtons(
    onSkip: () -> Unit,
    onNext: () -> Unit,
    nextButtonText: String = stringResource(R.string.tutorial_next)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("tutorialBottomButtons"),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onSkip) {
            Text(stringResource(R.string.tutorial_skip))
        }

        Button(onClick = onNext) {
            Text(nextButtonText)
        }
    }
}
