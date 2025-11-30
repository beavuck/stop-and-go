package com.beavuck.stop_and_go.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.beavuck.stop_and_go.dialogs.ColorPickerDialog
import com.beavuck.stop_and_go.utils.ColorUtils

class ColorPickerManager(
    private val fragmentManager: FragmentManager,
    private val getDefaultColor: (String) -> String
) {
    fun setupColorPicker(
        input: EditText,
        button: Button,
        requestKey: String,
        dialogTag: String
    ) {
        button.setOnClickListener {
            val colorValue = input.text.toString().trim()
            val colorToUse = colorValue.ifEmpty { getDefaultColor(requestKey) }
            ColorPickerDialog.newInstance(colorToUse, requestKey)
                .show(fragmentManager, dialogTag)
        }
        input.addTextChangedListener(createColorTextWatcher(button))
        updateButtonColor(button, input.text.toString())
    }

    private fun createColorTextWatcher(button: Button) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/*no op*/}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*no op*/}
        override fun afterTextChanged(s: Editable?) {
            updateButtonColor(button, s?.toString() ?: "")
        }
    }

    private fun updateButtonColor(button: Button, colorHex: String) {
        val color = ColorUtils.parseColorSafely(colorHex)
        button.setBackgroundColor(color)
    }
}
