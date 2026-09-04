package com.example.starkfuturetest.presentation.dashboard.formatter

import com.example.starkfuturetest.R
import com.example.starkfuturetest.core.resources.ResourcesRepository
import javax.inject.Inject

class TelemetryValueFormatterImpl @Inject constructor(
    private val resources: ResourcesRepository,
) : TelemetryValueFormatter {

    override fun formatTimestamp(isoTimestamp: String): String =
        try {
            isoTimestamp.replace("T", " ").take(16)
        } catch (e: Exception) {
            isoTimestamp
        }

    override fun formatPowerMap(powerMap: String): String =
        powerMap.replaceFirstChar { it.uppercaseChar() }

    override fun formatSpeed(kmh: Double): String =
        resources.getString(R.string.format_speed_kmh, kmh)

    override fun formatPower(hp: Double): String =
        resources.getString(R.string.format_power_hp, hp)

    override fun formatTemperature(celsius: Double): String =
        resources.getString(R.string.format_temperature_c, celsius)

    override fun formatDistance(km: Double): String =
        resources.getString(R.string.format_distance_km, km)

    override fun formatRange(km: Int): String =
        resources.getString(R.string.format_range_km, km)

    override fun formatPercentage(value: Int): String =
        resources.getString(R.string.format_percentage, value)
}