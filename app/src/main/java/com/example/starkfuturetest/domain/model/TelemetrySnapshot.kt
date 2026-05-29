package com.example.starkfuturetest.domain.model

data class TelemetrySnapshot(
    val bike: BikeInfo,
    val timestamp: String,
    val battery: BatteryInfo,
    val motor: MotorInfo,
    val rideSettings: RideSettings,
    val session: SessionInfo,
    val diagnostics: Diagnostics,
)