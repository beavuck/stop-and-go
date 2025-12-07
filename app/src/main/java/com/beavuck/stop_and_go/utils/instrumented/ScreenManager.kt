package com.beavuck.stop_and_go.utils.instrumented

import android.app.Activity
import android.view.WindowManager

class ScreenManager(private val activity: Activity) {
    fun setKeepScreenOn(keepOn: Boolean) {
        if (keepOn) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
