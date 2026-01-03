package com.beavuck.stop_and_go.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.beavuck.stop_and_go.R

class PhaseNotificationManager(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Suppress("unused", "RedundantSuppression")
    companion object {
        private const val GO_CHANNEL_ID = "go_phase_channel"
        private const val STOP_CHANNEL_ID = "stop_phase_channel"
        private const val GO_NOTIFICATION_ID = 1
        private const val STOP_NOTIFICATION_ID = 2
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            createChannel(
                GO_CHANNEL_ID,
                R.string.notification_channel_go_name,
                R.string.notification_channel_go_description,
                longArrayOf(0, 200, 100, 200),
                audioAttributes
            )

            createChannel(
                STOP_CHANNEL_ID,
                R.string.notification_channel_stop_name,
                R.string.notification_channel_stop_description,
                longArrayOf(0, 768),
                audioAttributes
            )
        }
    }

    private fun createChannel(
        channelId: String,
        nameResId: Int,
        descriptionResId: Int,
        pattern: LongArray,
        audioAttributes: AudioAttributes
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(nameResId),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(descriptionResId)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audioAttributes
                )
                enableVibration(true)
                vibrationPattern = pattern
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notifyGoPhase(customLabel: String = "") {
        notifyPhase(GO_CHANNEL_ID, GO_NOTIFICATION_ID, R.string.notification_go_title, customLabel)
    }

    fun notifyStopPhase(customLabel: String = "") {
        notifyPhase(
            STOP_CHANNEL_ID,
            STOP_NOTIFICATION_ID,
            R.string.notification_stop_title,
            customLabel
        )
    }

    private fun notifyPhase(
        channelId: String,
        notificationId: Int,
        titleResId: Int,
        customLabel: String
    ) {
        val title = customLabel.ifBlank {
            context.getString(titleResId)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(5000)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
