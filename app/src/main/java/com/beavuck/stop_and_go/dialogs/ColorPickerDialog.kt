package com.beavuck.stop_and_go.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.TimerConstants.DEFAULT_COLOR
import com.beavuck.stop_and_go.utils.ColorUtils

class ColorPickerDialog : DialogFragment() {

    private var currentColor: Int = Color.BLACK

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = layoutInflater.inflate(R.layout.dialog_color_picker, null)

        val initialColor = arguments?.getString(ARG_INITIAL_COLOR) ?: DEFAULT_COLOR
        currentColor = savedInstanceState?.getInt(STATE_CURRENT_COLOR)
            ?: ColorUtils.parseColorSafely(initialColor, Color.BLACK)

        val colorPreview = view.findViewById<View>(R.id.color_preview)
        val hexDisplay = view.findViewById<TextView>(R.id.hex_value_display)
        val redSeekBar = view.findViewById<SeekBar>(R.id.red_seekbar)
        val greenSeekBar = view.findViewById<SeekBar>(R.id.green_seekbar)
        val blueSeekBar = view.findViewById<SeekBar>(R.id.blue_seekbar)

        redSeekBar.progress = Color.red(currentColor)
        greenSeekBar.progress = Color.green(currentColor)
        blueSeekBar.progress = Color.blue(currentColor)

        updatePreview(colorPreview, hexDisplay, currentColor)

        val seekBarListener = createColorChangeListener(
            redSeekBar,
            greenSeekBar,
            blueSeekBar,
            colorPreview,
            hexDisplay
        )
        listOf(redSeekBar, greenSeekBar, blueSeekBar).forEach {
            it.setOnSeekBarChangeListener(
                seekBarListener
            )
        }

        return AlertDialog.Builder(context)
            .setTitle(R.string.color_picker_title)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ ->
                val requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: REQUEST_KEY
                setFragmentResult(
                    requestKey,
                    bundleOf(RESULT_COLOR to ColorUtils.colorToHex(currentColor))
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_COLOR, currentColor)
    }

    private fun createColorChangeListener(
        redSeekBar: SeekBar,
        greenSeekBar: SeekBar,
        blueSeekBar: SeekBar,
        colorPreview: View,
        hexDisplay: TextView
    ) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            currentColor =
                Color.rgb(redSeekBar.progress, greenSeekBar.progress, blueSeekBar.progress)
            updatePreview(colorPreview, hexDisplay, currentColor)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {/*no op*/
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {/*no op*/
        }
    }

    private fun updatePreview(preview: View, hexDisplay: TextView, color: Int) {
        preview.setBackgroundColor(color)
        hexDisplay.text = ColorUtils.colorToHex(color)
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
