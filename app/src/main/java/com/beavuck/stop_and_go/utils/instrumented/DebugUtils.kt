package com.beavuck.stop_and_go.utils.instrumented

import android.os.StrictMode
import com.beavuck.stop_and_go.BuildConfig

@Suppress("unused", "RedundantSuppression")
object DebugUtils {
    fun maybeSetStrictMode() {
        if (!BuildConfig.DEBUG) {
            return
        }
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyFlashScreen()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )
    }
}