package com.example.starkfuturetest.presentation.dashboard.formatter

import com.example.starkfuturetest.R
import com.example.starkfuturetest.core.resources.ResourcesRepository
import javax.inject.Inject

class DurationFormatterImpl @Inject constructor(
    private val resources: ResourcesRepository,
) : DurationFormatter {

    override fun format(durationSeconds: Int): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        return if (hours > 0) {
            val minutesPadded = minutes.toString().padStart(2, '0')
            resources.getString(R.string.format_duration_hm, hours, minutesPadded)
        } else {
            resources.getString(R.string.format_duration_m, minutes)
        }
    }
}