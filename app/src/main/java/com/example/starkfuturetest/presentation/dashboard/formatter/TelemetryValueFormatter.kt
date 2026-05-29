package com.example.starkfuturetest.presentation.dashboard.formatter

interface TelemetryValueFormatter {
    fun formatTimestamp(isoTimestamp: String): String
    fun formatPowerMap(powerMap: String): String
    fun formatSpeed(kmh: Double): String
    fun formatPower(hp: Double): String
    fun formatTemperature(celsius: Double): String
    fun formatDistance(km: Double): String
    fun formatPercentage(value: Int): String
}