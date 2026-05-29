package com.example.starkfuturetest.presentation.dashboard.formatter

import javax.inject.Inject

class DurationFormatterImpl @Inject constructor() : DurationFormatter {
    override fun format(durationSeconds: Int): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        return if (hours > 0) {
            "${hours}h ${minutes.toString().padStart(2, '0')}m"
        } else {
            "${minutes}m"
        }
    }
}