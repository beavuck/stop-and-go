package com.beavuck.stop_and_go.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent

class TimerGestureHandler(
    context: Context,
    private val onSingleTap: () -> Unit,
    private val onLongPress: () -> Unit
) {
    val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                onSingleTap()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                onLongPress()
            }
        })
}
