package com.example.starkfuturetest.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelemetrySnapshotDto(
    @SerialName("bike") val bike: BikeInfoDto,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("battery") val battery: BatteryInfoDto,
    @SerialName("motor") val motor: MotorInfoDto,
    @SerialName("ride_settings") val rideSettings: RideSettingsDto,
    @SerialName("session") val session: SessionInfoDto,
    @SerialName("diagnostics") val diagnostics: DiagnosticsDto,
)

@Serializable
data class BikeInfoDto(
    @SerialName("model") val model: String,
    @SerialName("variant") val variant: String,
    @SerialName("firmware_version") val firmwareVersion: String,
    @SerialName("image_url") val imageUrl: String,
)

@Serializable
data class BatteryInfoDto(
    @SerialName("state_of_charge_pct") val stateOfChargePct: Int,
    @SerialName("estimated_range_km") val estimatedRangeKm: Int,
    @SerialName("temperature_c") val temperatureC: Double,
    @SerialName("charging_state") val chargingState: String,
)

@Serializable
data class MotorInfoDto(
    @SerialName("power_hp") val powerHp: Double,
    @SerialName("temperature_c") val temperatureC: Double,
)

@Serializable
data class RideSettingsDto(
    @SerialName("power_map") val powerMap: String,
    @SerialName("max_power_hp") val maxPowerHp: Int,
    @SerialName("engine_braking_pct") val engineBrakingPct: Int,
    @SerialName("regen_pct") val regenPct: Int,
)

@Serializable
data class SessionInfoDto(
    @SerialName("duration_s") val durationS: Int,
    @SerialName("distance_km") val distanceKm: Double,
    @SerialName("max_speed_kmh") val maxSpeedKmh: Double,
)

@Serializable
data class DiagnosticsDto(
    @SerialName("fault_codes") val faultCodes: List<String> = emptyList(),
    @SerialName("warnings") val warnings: List<DiagnosticWarningDto> = emptyList(),
)

@Serializable
data class DiagnosticWarningDto(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("severity") val severity: String,
)