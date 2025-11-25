package com.beavuck.stop_and_go.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.beavuck.stop_and_go.R

class ColorPickerDialog : DialogFragment() {

    private var currentColor: Int = Color.BLACK

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = layoutInflater.inflate(R.layout.dialog_color_picker, null)

        val initialColor = arguments?.getString(ARG_INITIAL_COLOR) ?: "#000000"
        currentColor = if (savedInstanceState != null) {
            savedInstanceState.getInt(STATE_CURRENT_COLOR)
        } else {
            parseColor(initialColor)
        }

        val colorPreview = view.findViewById<View>(R.id.color_preview)
        val hexDisplay = view.findViewById<TextView>(R.id.hex_value_display)
        val redSeekBar = view.findViewById<SeekBar>(R.id.red_seekbar)
        val greenSeekBar = view.findViewById<SeekBar>(R.id.green_seekbar)
        val blueSeekBar = view.findViewById<SeekBar>(R.id.blue_seekbar)

        redSeekBar.progress = Color.red(currentColor)
        greenSeekBar.progress = Color.green(currentColor)
        blueSeekBar.progress = Color.blue(currentColor)

        updatePreview(colorPreview, hexDisplay, currentColor)

        val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentColor = Color.rgb(
                    redSeekBar.progress,
                    greenSeekBar.progress,
                    blueSeekBar.progress
                )
                updatePreview(colorPreview, hexDisplay, currentColor)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }
        }

        redSeekBar.setOnSeekBarChangeListener(seekBarListener)
        greenSeekBar.setOnSeekBarChangeListener(seekBarListener)
        blueSeekBar.setOnSeekBarChangeListener(seekBarListener)

        return AlertDialog.Builder(context)
            .setTitle(R.string.color_picker_title)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ ->
                val requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: REQUEST_KEY
                setFragmentResult(requestKey, bundleOf(RESULT_COLOR to colorToHex(currentColor)))
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_COLOR, currentColor)
    }

    private fun updatePreview(preview: View, hexDisplay: TextView, color: Int) {
        preview.setBackgroundColor(color)
        hexDisplay.text = colorToHex(color)
    }

    private fun parseColor(hex: String): Int {
        return try {
            hex.trim().toColorInt()
        } catch (_: IllegalArgumentException) {
            Color.BLACK
        }
    }

    private fun colorToHex(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }

    companion object {
        const val REQUEST_KEY = "color_picker_request"
        const val RESULT_COLOR = "selected_color"
        private const val ARG_INITIAL_COLOR = "initial_color"
        private const val ARG_REQUEST_KEY = "request_key"
        private const val STATE_CURRENT_COLOR = "current_color"

        fun newInstance(initialColor: String, requestKey: String = REQUEST_KEY) =
            ColorPickerDialog().apply {
                arguments = bundleOf(
                    ARG_INITIAL_COLOR to initialColor,
                    ARG_REQUEST_KEY to requestKey
                )
            }
    }
}
