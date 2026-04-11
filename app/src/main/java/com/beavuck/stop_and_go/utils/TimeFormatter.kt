package com.beavuck.stop_and_go.utils

enum class TimeFormat { HMS, MS, S }

data class TimeComponents(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val format: TimeFormat
)

fun splitTime(seconds: Int): TimeComponents {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    val format = when {
        hours > 0 -> TimeFormat.HMS
        minutes > 0 -> TimeFormat.MS
        else -> TimeFormat.S
    }
    return TimeComponents(hours, minutes, secs, format)
}
