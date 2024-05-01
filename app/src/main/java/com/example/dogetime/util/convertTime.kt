package com.example.dogetime.util

import java.util.concurrent.TimeUnit

fun convertTime(time: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(time)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours)
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(
            minutes
        )

    return if (hours == 0L) {
        "%02d:%02d".format(minutes, seconds)
    } else {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

}