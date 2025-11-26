package com.beavuck.stop_and_go.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.dialogs.ColorPickerDialog
import com.beavuck.stop_and_go.model.TimerConfig
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsActivity : AppCompatActivity() {
    private lateinit var configRepository: ConfigRepository
    private lateinit var stateRepository: StateRepository

    private lateinit var goDurationInput: EditText
    private lateinit var stopDurationInput: EditText
    private lateinit var goGrowthInput: EditText
    private lateinit var stopGrowthInput: EditText
    private lateinit var goColorInput: EditText
    private lateinit var stopColorInput: EditText
    private lateinit var goColorPickerButton: Button
    private lateinit var stopColorPickerButton: Button
    private lateinit var saveButton: FloatingActionButton
    private lateinit var resetButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        configRepository = ConfigRepository(this)
        stateRepository = StateRepository(this)

        bindViews()
        setupFragmentResultListeners()
        loadCurrentConfig()
        setupButtons()
        setupColorPickers()
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

    private fun bindViews() {
        goDurationInput = findViewById(R.id.go_duration_input)
        stopDurationInput = findViewById(R.id.stop_duration_input)
        goGrowthInput = findViewById(R.id.go_growth_input)
        stopGrowthInput = findViewById(R.id.stop_growth_input)
        goColorInput = findViewById(R.id.go_color_input)
        stopColorInput = findViewById(R.id.stop_color_input)
        goColorPickerButton = findViewById(R.id.go_color_picker_button)
        stopColorPickerButton = findViewById(R.id.stop_color_picker_button)
        saveButton = findViewById(R.id.save_button)
        resetButton = findViewById(R.id.reset_button)
    }

    private fun setupButtons() {
        saveButton.setOnClickListener { saveSettings() }
        resetButton.setOnClickListener { resetTimer() }
    }

    private fun setupColorPickers() {
        setupColorPicker(
            goColorInput,
            goColorPickerButton,
            REQUEST_KEY_GO_COLOR,
            DIALOG_TAG_GO_COLOR
        )
        setupColorPicker(
            stopColorInput,
            stopColorPickerButton,
            REQUEST_KEY_STOP_COLOR,
            DIALOG_TAG_STOP_COLOR
        )
    }

    private fun setupColorPicker(
        input: EditText,
        button: Button,
        requestKey: String,
        dialogTag: String
    ) {
        button.setOnClickListener {
            ColorPickerDialog.newInstance(input.text.toString(), requestKey)
                .show(supportFragmentManager, dialogTag)
        }
        input.addTextChangedListener(createColorTextWatcher(button))
        updateButtonColor(button, input.text.toString())
    }

    private fun createColorTextWatcher(button: Button) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/*no-op*/}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*no-op*/}
        override fun afterTextChanged(s: Editable?) {
            updateButtonColor(button, s?.toString() ?: "")
        }
    }

    private fun updateButtonColor(button: Button, colorHex: String) {
        try {
            val color = colorHex.trim().toColorInt()
            button.setBackgroundColor(color)
        } catch (_: IllegalArgumentException) {
            button.setBackgroundColor(Color.GRAY)
        }
    }

    private fun resetTimer() {
        stateRepository.clearState()
        Toast.makeText(this, R.string.reset_timer, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun loadCurrentConfig() {
        val config = configRepository.loadConfig()
        goDurationInput.setText(config.goDuration.toString())
        stopDurationInput.setText(config.stopDuration.toString())
        goGrowthInput.setText(config.goDurationGrowth.toString())
        stopGrowthInput.setText(config.stopDurationGrowth.toString())
        goColorInput.setText(config.goColor)
        stopColorInput.setText(config.stopColor)
    }

    private fun saveSettings() {
        val config = parseConfigFromInputs() ?: return

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

    private fun parseConfigFromInputs(): TimerConfig? {
        val goDuration = goDurationInput.text.toString().toIntOrNull()
        val stopDuration = stopDurationInput.text.toString().toIntOrNull()
        val goGrowth = goGrowthInput.text.toString().toFloatOrNull()
        val stopGrowth = stopGrowthInput.text.toString().toFloatOrNull()

        if (goDuration == null || stopDuration == null || goGrowth == null || stopGrowth == null) {
            showError()
            return null
        }

        return TimerConfig(
            goDuration = goDuration,
            stopDuration = stopDuration,
            goDurationGrowth = goGrowth,
            stopDurationGrowth = stopGrowth,
            goColor = goColorInput.text.toString().trim(),
            stopColor = stopColorInput.text.toString().trim()
        )
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
            dismissKeyboardIfTouchOutsideEditText(event)
        }
        return super.dispatchTouchEvent(event)
    }

    private fun dismissKeyboardIfTouchOutsideEditText(event: MotionEvent) {
        val view = currentFocus as? EditText ?: return
        val outRect = android.graphics.Rect()
        view.getGlobalVisibleRect(outRect)

        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            view.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {
        private const val REQUEST_KEY_GO_COLOR = "go_color_picker"
        private const val REQUEST_KEY_STOP_COLOR = "stop_color_picker"
        private const val DIALOG_TAG_GO_COLOR = "goColorPicker"
        private const val DIALOG_TAG_STOP_COLOR = "stopColorPicker"
    }
}
