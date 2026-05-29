package com.example.starkfuturetest.presentation.dashboard.formatter

import javax.inject.Inject

class TelemetryValueFormatterImpl @Inject constructor() : TelemetryValueFormatter {

    override fun formatTimestamp(isoTimestamp: String): String =
        try {
            isoTimestamp.replace("T", " ").take(16)
        } catch (e: Exception) {
            isoTimestamp
        }

    override fun formatPowerMap(powerMap: String): String =
        powerMap.replaceFirstChar { it.uppercaseChar() }

    override fun formatSpeed(kmh: Double): String = "%.1f km/h".format(kmh)

    override fun formatPower(hp: Double): String = "%.1f hp".format(hp)

    override fun formatTemperature(celsius: Double): String = "%.1f°C".format(celsius)

    override fun formatDistance(km: Double): String = "%.1f km".format(km)

    override fun formatPercentage(value: Int): String = "$value%"
}