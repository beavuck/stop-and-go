package com.beavuck.stop_and_go.ui.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.activities.TutorialActivity
import com.beavuck.stop_and_go.dialogs.ColorPickerDialog
import com.beavuck.stop_and_go.dialogs.LanguagePickerDialog
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    configRepository: ConfigRepository,
    stateRepository: StateRepository,
    tutorialRepository: TutorialRepository,
    currentLocale: String?,
    onLocaleChange: (String) -> Unit,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    val currentConfig = remember { configRepository.loadConfig() }

    val invalidInputMessage = stringResource(R.string.invalid_input)
    val settingsSavedMessage = stringResource(R.string.settings_saved)
    val resetTimerMessage = stringResource(R.string.reset_timer)

    var goDuration by rememberSaveable { mutableStateOf(currentConfig.goDuration.toString()) }
    var stopDuration by rememberSaveable { mutableStateOf(currentConfig.stopDuration.toString()) }
    var goGrowth by rememberSaveable { mutableStateOf(currentConfig.goDurationGrowth.toString()) }
    var stopGrowth by rememberSaveable { mutableStateOf(currentConfig.stopDurationGrowth.toString()) }
    var goColor by rememberSaveable { mutableStateOf(currentConfig.goColor) }
    var stopColor by rememberSaveable { mutableStateOf(currentConfig.stopColor) }
    var goLabel by rememberSaveable { mutableStateOf(currentConfig.goLabel) }
    var stopLabel by rememberSaveable { mutableStateOf(currentConfig.stopLabel) }

    var showGoColorPicker by rememberSaveable { mutableStateOf(false) }
    var showStopColorPicker by rememberSaveable { mutableStateOf(false) }
    var showLanguagePicker by rememberSaveable { mutableStateOf(false) }

    fun saveSettings() {
        val config = TimerConfig(
            goDuration = goDuration.toIntOrNull() ?: currentConfig.goDuration,
            stopDuration = stopDuration.toIntOrNull() ?: currentConfig.stopDuration,
            goDurationGrowth = goGrowth.toFloatOrNull() ?: currentConfig.goDurationGrowth,
            stopDurationGrowth = stopGrowth.toFloatOrNull() ?: currentConfig.stopDurationGrowth,
            goColor = ColorUtils.isValidColorString(goColor)
                .let { if (it) goColor else currentConfig.goColor },
            stopColor = ColorUtils.isValidColorString(stopColor)
                .let { if (it) stopColor else currentConfig.stopColor },
            goLabel = goLabel.trim(),
            stopLabel = stopLabel.trim(),
        )

        try {
            config.validate(context)
            configRepository.saveConfig(config)
            stateRepository.clearState()
            Toast.makeText(context, settingsSavedMessage, Toast.LENGTH_SHORT).show()
            onFinish()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                context,
                e.message ?: invalidInputMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun resetTimer() {
        stateRepository.clearState()
        Toast.makeText(context, resetTimerMessage, Toast.LENGTH_SHORT).show()
        onFinish()
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings))
                },
                actions = {
                    IconButton(
                        onClick = { resetTimer() },
                        modifier = Modifier.testTag("resetButton")
                    ) {
                        Icon(
                            painterResource(R.drawable.reset),
                            contentDescription = stringResource(R.string.reset_timer)
                        )
                    }
                    IconButton(
                        onClick = {
                            tutorialRepository.resetTutorialCompletion()
                            val intent = Intent(context, TutorialActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("helpButton")
                    ) {
                        Icon(
                            painterResource(R.drawable.help),
                            contentDescription = stringResource(R.string.help)
                        )
                    }
                    IconButton(
                        onClick = { showLanguagePicker = true },
                        modifier = Modifier.testTag("languageButton")
                    ) {
                        Icon(
                            painterResource(R.drawable.languages),
                            contentDescription = stringResource(R.string.language)
                        )
                    }
                    IconButton(
                        onClick = { saveSettings() },
                        modifier = Modifier.testTag("saveButton")
                    ) {
                        Icon(
                            painterResource(R.drawable.check_circle),
                            contentDescription = stringResource(R.string.save_settings)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsField(
                label = stringResource(R.string.go_duration),
                value = goDuration,
                onValueChange = { goDuration = it },
                onDone = ::saveSettings,
                testTag = "goDurationInput",
                supportingText = stringResource(R.string.go_duration_hint),
                keyboardType = KeyboardType.Number,
            )

            SettingsField(
                label = stringResource(R.string.stop_duration),
                value = stopDuration,
                onValueChange = { stopDuration = it },
                onDone = ::saveSettings,
                testTag = "stopDurationInput",
                supportingText = stringResource(R.string.stop_duration_hint),
                keyboardType = KeyboardType.Number,
            )

            SettingsField(
                label = stringResource(R.string.go_growth),
                value = goGrowth,
                onValueChange = { goGrowth = it },
                onDone = ::saveSettings,
                testTag = "goGrowthInput",
                supportingText = stringResource(R.string.go_growth_hint),
                keyboardType = KeyboardType.Number,
            )

            SettingsField(
                label = stringResource(R.string.stop_growth),
                value = stopGrowth,
                onValueChange = { stopGrowth = it },
                onDone = ::saveSettings,
                testTag = "stopGrowthInput",
                supportingText = stringResource(R.string.stop_growth_hint),
                keyboardType = KeyboardType.Number,
            )

            ColorInputField(
                label = stringResource(R.string.go_color),
                color = goColor,
                onValueChange = { goColor = it },
                onPickerClick = { showGoColorPicker = true },
                onDone = ::saveSettings,
                testTag = "goColorInput",
                buttonTestTag = "goColorButton",
                supportingText = stringResource(R.string.go_color_hint),
            )

            ColorInputField(
                label = stringResource(R.string.stop_color),
                color = stopColor,
                onValueChange = { stopColor = it },
                onPickerClick = { showStopColorPicker = true },
                onDone = ::saveSettings,
                testTag = "stopColorInput",
                buttonTestTag = "stopColorButton",
                supportingText = stringResource(R.string.stop_color_hint),
            )

            SettingsField(
                label = stringResource(R.string.go_label),
                value = goLabel,
                onValueChange = { goLabel = it },
                onDone = ::saveSettings,
                testTag = "goLabelInput",
                supportingText = stringResource(R.string.go_label_hint),
            )

            SettingsField(
                label = stringResource(R.string.stop_label),
                value = stopLabel,
                onValueChange = { stopLabel = it },
                onDone = ::saveSettings,
                testTag = "stopLabelInput",
                supportingText = stringResource(R.string.stop_label_hint),
            )
        }
    }

    if (showGoColorPicker) {
        ColorPickerDialog(
            initialColor = goColor,
            onColorSelected = { color ->
                goColor = color
                showGoColorPicker = false
            },
            onDismiss = { showGoColorPicker = false }
        )
    }

    if (showStopColorPicker) {
        ColorPickerDialog(
            initialColor = stopColor,
            onColorSelected = { color ->
                stopColor = color
                showStopColorPicker = false
            },
            onDismiss = { showStopColorPicker = false }
        )
    }

    if (showLanguagePicker) {
        LanguagePickerDialog(
            currentLocale = SupportedLocale.fromCode(currentLocale),
            onLocaleSelected = { code ->
                onLocaleChange(code)
                showLanguagePicker = false
            },
            onDismiss = { showLanguagePicker = false }
        )
    }
}

@Composable
private fun SettingsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    testTag: String,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

@Composable
private fun ColorInputField(
    label: String,
    color: String,
    onValueChange: (String) -> Unit,
    onPickerClick: () -> Unit,
    onDone: () -> Unit,
    testTag: String,
    buttonTestTag: String,
    supportingText: String? = null,
) {
    val pickColorDescription = stringResource(R.string.pick_color)

    Column(modifier = Modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = color,
                onValueChange = onValueChange,
                supportingText = supportingText?.let { { Text(it) } },
                modifier = Modifier
                    .weight(1f)
                    .testTag(testTag),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDone() }
                ),
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color(ColorUtils.parseColorSafely(color)),
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .clickable(
                        onClickLabel = pickColorDescription
                    ) { onPickerClick() }
                    .testTag(buttonTestTag)
            )
        }
    }
}
