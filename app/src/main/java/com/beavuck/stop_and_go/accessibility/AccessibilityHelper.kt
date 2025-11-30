package com.beavuck.stop_and_go.accessibility

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.PhaseState

class AccessibilityHelper(
    private val context: Context,
    private val mainLayout: ConstraintLayout,
    private val phaseLabelText: TextView
) {
    fun setupAccessibilityActions(isPaused: () -> Boolean) {
        ViewCompat.setAccessibilityDelegate(
            mainLayout,
            object : androidx.core.view.AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)

                    val pauseResumeAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                        AccessibilityNodeInfoCompat.ACTION_CLICK,
                        if (isPaused()) {
                            context.getString(R.string.timer_resumed)
                        } else {
                            context.getString(R.string.timer_paused)
                        }
                    )
                    info.addAction(pauseResumeAction)

                    val settingsAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                        AccessibilityNodeInfoCompat.ACTION_LONG_CLICK,
                        context.getString(R.string.settings)
                    )
                    info.addAction(settingsAction)
                }
            })

        mainLayout.isClickable = true
        mainLayout.isFocusable = true
    }

    fun announcePauseState(isPaused: Boolean) {
        val message = if (isPaused) {
            context.getString(R.string.timer_paused)
        } else {
            context.getString(R.string.timer_resumed)
        }
        ViewCompat.setStateDescription(mainLayout, message)
    }

    fun announcePhaseChange(phase: PhaseState) {
        val phaseLabel = if (phase.isGo) {
            context.getString(R.string.phase_go)
        } else {
            context.getString(R.string.phase_stop)
        }
        val message = context.getString(R.string.phase_changed, phaseLabel, phase.durationSeconds)
        ViewCompat.setStateDescription(phaseLabelText, message)
    }
}
