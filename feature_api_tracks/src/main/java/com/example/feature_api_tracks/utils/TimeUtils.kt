package com.example.feature_api_tracks.utils

import android.annotation.SuppressLint

object TimeUtils {
    private const val SECONDS_IN_MINUTE = 60
    private const val TIME_FORMAT = "%02d:%02d"

    @SuppressLint("DefaultLocale")
    fun formatDuration(durationInSeconds: Int): String {
        val minutes = durationInSeconds / SECONDS_IN_MINUTE
        val seconds = durationInSeconds % SECONDS_IN_MINUTE
        return String.format(TIME_FORMAT, minutes, seconds)
    }
}