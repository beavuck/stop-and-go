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
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.dialogs.ColorPickerDialog
import com.beavuck.stop_and_go.dialogs.ConfigPickerDialog
import com.beavuck.stop_and_go.dialogs.LanguagePickerDialog
import com.beavuck.stop_and_go.dialogs.MoreDialog
import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEBOUNCE_DELAY
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import com.beavuck.stop_and_go.utils.instrumented.ColorUtils
import kotlinx.coroutines.delay


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
    val currentConfig = remember { mutableStateOf(configRepository.loadConfig()) }

    val invalidInputMessage = stringResource(R.string.invalid_input)
    val settingsSavedMessage = stringResource(R.string.settings_saved)
    val resetTimerMessage = stringResource(R.string.reset_timer)

    val goDuration = rememberSaveable { mutableStateOf(currentConfig.value.goDuration.toString()) }
    val stopDuration =
        rememberSaveable { mutableStateOf(currentConfig.value.stopDuration.toString()) }
    val goGrowth =
        rememberSaveable { mutableStateOf(currentConfig.value.goDurationGrowth.toString()) }
    val stopGrowth =
        rememberSaveable { mutableStateOf(currentConfig.value.stopDurationGrowth.toString()) }
    val goColor = rememberSaveable { mutableStateOf(currentConfig.value.goColor) }
    val stopColor = rememberSaveable { mutableStateOf(currentConfig.value.stopColor) }
    val goLabel = rememberSaveable { mutableStateOf(currentConfig.value.goLabel) }
    val stopLabel = rememberSaveable { mutableStateOf(currentConfig.value.stopLabel) }
    val soundEnabled = rememberSaveable { mutableStateOf(currentConfig.value.soundEnabled) }

    val showGoColorPicker = rememberSaveable { mutableStateOf(false) }
    val showStopColorPicker = rememberSaveable { mutableStateOf(false) }
    val showLanguagePicker = rememberSaveable { mutableStateOf(false) }
    val showMoreDialog = rememberSaveable { mutableStateOf(false) }
    val showConfigPicker = rememberSaveable { mutableStateOf(false) }

    val isInitialComposition = remember { mutableStateOf(true) }

    fun buildConfig() = TimerConfig(
        goDuration = goDuration.value.toIntOrNull() ?: currentConfig.value.goDuration,
        stopDuration = stopDuration.value.toIntOrNull() ?: currentConfig.value.stopDuration,
        goDurationGrowth = goGrowth.value.toFloatOrNull() ?: currentConfig.value.goDurationGrowth,
        stopDurationGrowth = stopGrowth.value.toFloatOrNull()
            ?: currentConfig.value.stopDurationGrowth,
        goColor = ColorUtils.isValidColorString(goColor.value).let { if (it) goColor.value else currentConfig.value.goColor },
        stopColor = ColorUtils.isValidColorString(stopColor.value).let { if (it) stopColor.value else currentConfig.value.stopColor },
        goLabel = goLabel.value.trim(),
        stopLabel = stopLabel.value.trim(),
        soundEnabled = soundEnabled.value,
    )

    fun saveSettingsInternal(showToast: Boolean = false, finishAfter: Boolean = false) {
        try {
            val config = buildConfig()
            config.validate(context)
            configRepository.saveConfig(config)
            stateRepository.clearState()
            if (showToast) {
                Toast.makeText(context, settingsSavedMessage, Toast.LENGTH_SHORT).show()
            }
            if (finishAfter) {
                onFinish()
            }
        } catch (e: IllegalArgumentException) {
            if (showToast) {
                Toast.makeText(
                    context,
                    e.message ?: invalidInputMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun autoSaveSettings() = saveSettingsInternal(showToast = false, finishAfter = false)

    fun saveSettings() = saveSettingsInternal(showToast = true, finishAfter = true)

    fun resetTimer() {
        Toast.makeText(context, resetTimerMessage, Toast.LENGTH_SHORT).show()
        stateRepository.clearState()
        onFinish()
    }

    fun reloadConfig() {
        isInitialComposition.value = true
        val reloaded = configRepository.loadConfig()
        currentConfig.value = reloaded
        goDuration.value = reloaded.goDuration.toString()
        stopDuration.value = reloaded.stopDuration.toString()
        goGrowth.value = reloaded.goDurationGrowth.toString()
        stopGrowth.value = reloaded.stopDurationGrowth.toString()
        goColor.value = reloaded.goColor
        stopColor.value = reloaded.stopColor
        goLabel.value = reloaded.goLabel
        stopLabel.value = reloaded.stopLabel
        soundEnabled.value = reloaded.soundEnabled
        stateRepository.clearState()
    }

    LaunchedEffect(
        goDuration.value,
        stopDuration.value,
        goGrowth.value,
        stopGrowth.value,
        goColor.value,
        stopColor.value,
        goLabel.value,
        stopLabel.value,
        soundEnabled.value,
    ) {
        if (isInitialComposition.value) {
            isInitialComposition.value = false
        } else {
            delay(DEBOUNCE_DELAY)
            autoSaveSettings()
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(
                        onClick = { resetTimer() },
                        modifier = Modifier.testTag("resetButton")
                    ) {
                        Icon(
                            painterResource(R.drawable.arrow_rewind),
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
                            painterResource(R.drawable.buoy),
                            contentDescription = stringResource(R.string.help)
                        )
                    }
                    IconButton(
                        onClick = { showConfigPicker.value = true },
                        modifier = Modifier.testTag("configPickerButton")
                    ) {
                        Icon(
                            painterResource(R.drawable.swap),
                            contentDescription = stringResource(R.string.config_picker_title)
                        )
                    }
                    IconButton(
                        onClick = { showLanguagePicker.value = true },
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
                    IconButton(
                        onClick = { showMoreDialog.value = true },
                        modifier = Modifier.testTag("moreButton")
                    ) {
                        Icon(
                            painterResource(R.drawable.vertical_dots),
                            contentDescription = stringResource(R.string.more)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sound_enabled),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = soundEnabled.value,
                    onCheckedChange = { soundEnabled.value = it },
                    modifier = Modifier.testTag("soundEnabledToggle")
                )
            }

            SettingsField(
                label = stringResource(R.string.go_duration),
                value = goDuration.value,
                onValueChange = { goDuration.value = it },
                onDone = ::saveSettings,
                testTag = "goDurationInput",
                supportingText = stringResource(R.string.go_duration_hint),
                keyboardType = KeyboardType.Number,
            )

            SettingsField(
                label = stringResource(R.string.stop_duration),
                value = stopDuration.value,
                onValueChange = { stopDuration.value = it },
                onDone = ::saveSettings,
                testTag = "stopDurationInput",
                supportingText = stringResource(R.string.stop_duration_hint),
                keyboardType = KeyboardType.Number,
            )

            SettingsField(
                label = stringResource(R.string.go_growth),
                value = goGrowth.value,
                onValueChange = { goGrowth.value = it },
                onDone = ::saveSettings,
                testTag = "goGrowthInput",
                supportingText = stringResource(R.string.go_growth_hint),
                keyboardType = KeyboardType.Number,
            )

            SettingsField(
                label = stringResource(R.string.stop_growth),
                value = stopGrowth.value,
                onValueChange = { stopGrowth.value = it },
                onDone = ::saveSettings,
                testTag = "stopGrowthInput",
                supportingText = stringResource(R.string.stop_growth_hint),
                keyboardType = KeyboardType.Number,
            )

            ColorInputField(
                label = stringResource(R.string.go_color),
                color = goColor.value,
                onValueChange = { goColor.value = it },
                onPickerClick = { showGoColorPicker.value = true },
                onDone = ::saveSettings,
                testTag = "goColorInput",
                buttonTestTag = "goColorButton",
                supportingText = stringResource(R.string.go_color_hint),
            )

            ColorInputField(
                label = stringResource(R.string.stop_color),
                color = stopColor.value,
                onValueChange = { stopColor.value = it },
                onPickerClick = { showStopColorPicker.value = true },
                onDone = ::saveSettings,
                testTag = "stopColorInput",
                buttonTestTag = "stopColorButton",
                supportingText = stringResource(R.string.stop_color_hint),
            )

            SettingsField(
                label = stringResource(R.string.go_label),
                value = goLabel.value,
                onValueChange = { goLabel.value = it },
                onDone = ::saveSettings,
                testTag = "goLabelInput",
                supportingText = stringResource(R.string.go_label_hint),
            )

            SettingsField(
                label = stringResource(R.string.stop_label),
                value = stopLabel.value,
                onValueChange = { stopLabel.value = it },
                onDone = ::saveSettings,
                testTag = "stopLabelInput",
                supportingText = stringResource(R.string.stop_label_hint),
            )
        }
    }

    if (showConfigPicker.value) {
        ConfigPickerDialog(
            configRepository = configRepository,
            onConfigChanged = {
                reloadConfig()
                showConfigPicker.value = false
            },
            onDismiss = { showConfigPicker.value = false }
        )
    }


    if (showGoColorPicker.value) {
        ColorPickerDialog(
            initialColor = goColor.value,
            onColorSelected = { color ->
                goColor.value = color
                showGoColorPicker.value = false
            },
            onDismiss = { showGoColorPicker.value = false }
        )
    }

    if (showStopColorPicker.value) {
        ColorPickerDialog(
            initialColor = stopColor.value,
            onColorSelected = { color ->
                stopColor.value = color
                showStopColorPicker.value = false
            },
            onDismiss = { showStopColorPicker.value = false }
        )
    }

    if (showLanguagePicker.value) {
        LanguagePickerDialog(
            currentLocale = SupportedLocale.fromCode(currentLocale),
            onLocaleSelected = { code ->
                onLocaleChange(code)
                showLanguagePicker.value = false
                onFinish()
            },
            onDismiss = { showLanguagePicker.value = false }
        )
    }

    if (showMoreDialog.value) {
        MoreDialog(
            configRepository = configRepository,
            stateRepository = stateRepository,
            onDismiss = { showMoreDialog.value = false },
            onReset = {
                val resetConfig = configRepository.loadConfig()
                currentConfig.value = resetConfig
                goDuration.value = resetConfig.goDuration.toString()
                stopDuration.value = resetConfig.stopDuration.toString()
                goGrowth.value = resetConfig.goDurationGrowth.toString()
                stopGrowth.value = resetConfig.stopDurationGrowth.toString()
                goColor.value = resetConfig.goColor
                stopColor.value = resetConfig.stopColor
                goLabel.value = resetConfig.goLabel
                stopLabel.value = resetConfig.stopLabel
                soundEnabled.value = resetConfig.soundEnabled
                showMoreDialog.value = false
            },
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
