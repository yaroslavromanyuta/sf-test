package com.example.starkfuturetest.presentation.dashboard

import com.example.starkfuturetest.domain.model.BatteryStatus

data class TelemetryDashboardUiModel(
    val bikeModel: String,
    val variant: String,
    val firmwareVersion: String,
    val formattedTimestamp: String,
    val imageUrl: String,
    val battery: BatteryUiModel,
    val motor: MotorUiModel,
    val rideSettings: RideSettingsUiModel,
    val session: SessionUiModel,
    val warnings: List<WarningUiModel>,
    val faultCodes: List<String>,
)

data class BatteryUiModel(
    val stateOfChargePct: Int,
    val displayCharge: String,
    val chargingState: String,
    val estimatedRange: String,
    val temperatureC: String,
    val batteryStatus: BatteryStatus,
)

data class MotorUiModel(
    val power: String,
    val temperatureC: String,
)

data class RideSettingsUiModel(
    val powerMap: String,
    val maxPower: String,
    val engineBraking: String,
    val regen: String,
)

data class SessionUiModel(
    val duration: String,
    val distance: String,
    val maxSpeed: String,
    val averageSpeed: String,
)

data class WarningUiModel(
    val code: String,
    val message: String,
    val severity: String,
)