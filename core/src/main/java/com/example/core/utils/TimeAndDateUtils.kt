package com.example.core.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Locale

object TimeAndDateUtils {
    private const val SECONDS_IN_MINUTE = 60
    private const val MILLISECONDS_IN_SECOND = 1_000
    private const val TIME_FORMAT = "%02d:%02d"
    private const val UNKNOWN_DATE = "Unknown"
    private const val INPUT_DATE_FORMAT = "yyyy-MM-dd"
    private const val OUTPUT_DATE_FORMAT = "dd.MM.yyyy"

    @SuppressLint("DefaultLocale")
    fun formatDuration(durationInSeconds: Int): String {
        val minutes = durationInSeconds / SECONDS_IN_MINUTE
        val seconds = durationInSeconds % SECONDS_IN_MINUTE
        return String.format(TIME_FORMAT, minutes, seconds)
    }

    @SuppressLint("DefaultLocale")
    fun formatTimeFromMillis(ms: Int): String {
        val seconds = ms / MILLISECONDS_IN_SECOND
        val minutes = seconds / SECONDS_IN_MINUTE
        return String.format(TIME_FORMAT, minutes, seconds % SECONDS_IN_MINUTE)
    }

    fun formatReleaseDate(date: String?): String {
        if (date.isNullOrEmpty()) return UNKNOWN_DATE

        return try {
            val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            parsedDate?.let { outputFormat.format(it) } ?: UNKNOWN_DATE
        } catch (e: Exception) {
            UNKNOWN_DATE
        }
    }
}
