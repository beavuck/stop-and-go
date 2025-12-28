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

    var red by rememberSaveable { mutableFloatStateOf(Color.red(parsedColor).toFloat()) }
    var green by rememberSaveable { mutableFloatStateOf(Color.green(parsedColor).toFloat()) }
    var blue by rememberSaveable { mutableFloatStateOf(Color.blue(parsedColor).toFloat()) }

    val currentColor by remember(red, green, blue) {
        mutableIntStateOf(Color.rgb(red.toInt(), green.toInt(), blue.toInt()))
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
                    value = red,
                    onValueChange = { red = it },
                    contentDescription = stringResource(R.string.red_slider),
                    testTag = "redSlider"
                )

                ColorChannelSlider(
                    label = stringResource(R.string.green_label),
                    value = green,
                    onValueChange = { green = it },
                    contentDescription = stringResource(R.string.green_slider),
                    testTag = "greenSlider"
                )

                ColorChannelSlider(
                    label = stringResource(R.string.blue_label),
                    value = blue,
                    onValueChange = { blue = it },
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
