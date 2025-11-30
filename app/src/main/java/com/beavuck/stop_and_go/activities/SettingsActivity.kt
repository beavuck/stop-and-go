package com.beavuck.stop_and_go.activities

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.dialogs.ColorPickerDialog
import com.beavuck.stop_and_go.dialogs.LanguagePickerDialog
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.beavuck.stop_and_go.settings.SettingsInputParser
import com.beavuck.stop_and_go.ui.ColorPickerManager
import com.beavuck.stop_and_go.ui.KeyboardManager
import com.beavuck.stop_and_go.utils.DebugUtils.maybeSetStrictMode
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsActivity : LocalizedActivity() {
    private lateinit var configRepository: ConfigRepository
    private lateinit var stateRepository: StateRepository
    private lateinit var inputParser: SettingsInputParser
    private lateinit var colorPickerManager: ColorPickerManager
    private lateinit var keyboardManager: KeyboardManager

    private lateinit var scrollView: ScrollView
    private lateinit var goDurationInput: EditText
    private lateinit var stopDurationInput: EditText
    private lateinit var goGrowthInput: EditText
    private lateinit var stopGrowthInput: EditText
    private lateinit var goColorInput: EditText
    private lateinit var stopColorInput: EditText
    private lateinit var goColorPickerButton: Button
    private lateinit var stopColorPickerButton: Button
    private lateinit var languageButton: FloatingActionButton
    private lateinit var saveButton: FloatingActionButton
    private lateinit var resetButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        maybeSetStrictMode()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        configRepository = ConfigRepository(this)
        stateRepository = StateRepository(this)

        bindViews()
        initializeHelpers()
        setupFragmentResultListeners()
        inputParser.loadConfig(configRepository.loadConfig())
        setupButtons()
        setupColorPickers()
    }

    private fun bindViews() {
        scrollView = findViewById(R.id.settings_scroll_view)
        goDurationInput = findViewById(R.id.go_duration_input)
        stopDurationInput = findViewById(R.id.stop_duration_input)
        goGrowthInput = findViewById(R.id.go_growth_input)
        stopGrowthInput = findViewById(R.id.stop_growth_input)
        goColorInput = findViewById(R.id.go_color_input)
        stopColorInput = findViewById(R.id.stop_color_input)
        goColorPickerButton = findViewById(R.id.go_color_picker_button)
        stopColorPickerButton = findViewById(R.id.stop_color_picker_button)
        languageButton = findViewById(R.id.language_button)
        saveButton = findViewById(R.id.save_button)
        resetButton = findViewById(R.id.reset_button)
    }

    private fun initializeHelpers() {
        inputParser = SettingsInputParser(
            goDurationInput, stopDurationInput,
            goGrowthInput, stopGrowthInput,
            goColorInput, stopColorInput
        )
        colorPickerManager = ColorPickerManager(
            supportFragmentManager,
            getDefaultColor = { requestKey ->
                val config = configRepository.loadConfig()
                if (requestKey == REQUEST_KEY_GO_COLOR) config.goColor else config.stopColor
            }
        )
        keyboardManager = KeyboardManager(this)
        keyboardManager.setupAutoScroll(
            scrollView,
            listOf(
                goDurationInput, stopDurationInput,
                goGrowthInput, stopGrowthInput,
                goColorInput, stopColorInput
            )
        )
    }

    private fun setupFragmentResultListeners() {
        setColorPickerResultListener(REQUEST_KEY_GO_COLOR, goColorInput)
        setColorPickerResultListener(REQUEST_KEY_STOP_COLOR, stopColorInput)
    }

    private fun setColorPickerResultListener(requestKey: String, targetInput: EditText) {
        supportFragmentManager.setFragmentResultListener(requestKey, this) { _, bundle ->
            bundle.getString(ColorPickerDialog.RESULT_COLOR)?.let { targetInput.setText(it) }
        }
    }

    private fun setupButtons() {
        languageButton.setOnClickListener { openLanguageDialog() }
        saveButton.setOnClickListener { saveSettings() }
        resetButton.setOnClickListener { resetTimer() }
    }

    private fun openLanguageDialog() {
        LanguagePickerDialog.newInstance().show(supportFragmentManager, "languagePicker")
    }

    private fun setupColorPickers() {
        colorPickerManager.setupColorPicker(
            goColorInput,
            goColorPickerButton,
            REQUEST_KEY_GO_COLOR,
            DIALOG_TAG_GO_COLOR
        )
        colorPickerManager.setupColorPicker(
            stopColorInput,
            stopColorPickerButton,
            REQUEST_KEY_STOP_COLOR,
            DIALOG_TAG_STOP_COLOR
        )
    }

    private fun resetTimer() {
        stateRepository.clearState()
        Toast.makeText(this, R.string.reset_timer, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun saveSettings() {
        val config = inputParser.parseConfig()

        try {
            config.validate()
            configRepository.saveConfig(config)
            stateRepository.clearState()
            showSuccess()
            finish()
        } catch (e: IllegalArgumentException) {
            showError(e.message)
        }
    }

    private fun showSuccess() {
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String? = null) {
        val errorMessage = message ?: getString(R.string.invalid_input)
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            keyboardManager.dismissKeyboardIfTouchOutside(event)
        }
        return super.dispatchTouchEvent(event)
    }

    companion object {
        private const val REQUEST_KEY_GO_COLOR = "go_color_picker"
        private const val REQUEST_KEY_STOP_COLOR = "stop_color_picker"
        private const val DIALOG_TAG_GO_COLOR = "goColorPicker"
        private const val DIALOG_TAG_STOP_COLOR = "stopColorPicker"
    }
}
