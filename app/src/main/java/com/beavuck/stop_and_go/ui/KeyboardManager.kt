package com.beavuck.stop_and_go.ui

import android.app.Activity
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import com.beavuck.stop_and_go.model.TimerConstants.APPROX_KEYBOARD_HEIGHT

class KeyboardManager(private val activity: Activity) {
    fun setupAutoScroll(scrollView: ScrollView, editTexts: List<EditText>) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    scrollView.post {
                        val location = IntArray(2)
                        view.getLocationInWindow(location)
                        val yCoordinatesIndex = 1
                        val y = location[yCoordinatesIndex]
                        scrollView.smoothScrollTo(0, y - APPROX_KEYBOARD_HEIGHT)
                    }
                }
            }
        }
    }

    fun dismissKeyboardIfTouchOutside(event: MotionEvent): Boolean {
        val view = activity.currentFocus as? EditText ?: return false
        val outRect = Rect()
        view.getGlobalVisibleRect(outRect)

        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            view.clearFocus()
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}
