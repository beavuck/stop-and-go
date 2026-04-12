package com.beavuck.stop_and_go.dialogs

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils

@Composable
fun ColorPickerDialog(
    initialColor: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val parsedColor = ColorUtils.parseColorSafely(initialColor)

    val red = rememberSaveable { mutableFloatStateOf(Color.red(parsedColor).toFloat()) }
    val green = rememberSaveable { mutableFloatStateOf(Color.green(parsedColor).toFloat()) }
    val blue = rememberSaveable { mutableFloatStateOf(Color.blue(parsedColor).toFloat()) }

    val currentColor by remember(red.floatValue, green.floatValue, blue.floatValue) {
        mutableIntStateOf(
            Color.rgb(
                red.floatValue.toInt(),
                green.floatValue.toInt(),
                blue.floatValue.toInt()
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                val colorPreviewDescription = stringResource(R.string.color_preview)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(androidx.compose.ui.graphics.Color(currentColor))
                        .semantics {
                            contentDescription = colorPreviewDescription
                        }
                        .testTag("colorPreview")
                )

                val hexValueDescription = stringResource(R.string.hint_hex_value_display)
                Text(
                    text = ColorUtils.getHexFromColorInt(currentColor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .semantics {
                            contentDescription = hexValueDescription
                        }
                        .testTag("hexDisplay")
                )

                ColorChannelSlider(
                    label = stringResource(R.string.red_label),
                    value = red.floatValue,
                    onValueChange = { red.floatValue = it },
                    contentDescription = stringResource(R.string.red_slider),
                    testTag = "redSlider"
                )

                ColorChannelSlider(
                    label = stringResource(R.string.green_label),
                    value = green.floatValue,
                    onValueChange = { green.floatValue = it },
                    contentDescription = stringResource(R.string.green_slider),
                    testTag = "greenSlider"
                )

                ColorChannelSlider(
                    label = stringResource(R.string.blue_label),
                    value = blue.floatValue,
                    onValueChange = { blue.floatValue = it },
                    contentDescription = stringResource(R.string.blue_slider),
                    testTag = "blueSlider"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onColorSelected(ColorUtils.getHexFromColorInt(currentColor)) },
                modifier = Modifier.testTag("confirmButton")
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancelButton")
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ColorChannelSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    contentDescription: String,
    testTag: String,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium
    )
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 0f..255f,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
            }
            .testTag(testTag)
    )
}
