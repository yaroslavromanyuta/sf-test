package com.example.starkfuturetest.data.mapper

import com.example.starkfuturetest.data.model.BatteryInfoDto
import com.example.starkfuturetest.data.model.BikeInfoDto
import com.example.starkfuturetest.data.model.DiagnosticWarningDto
import com.example.starkfuturetest.data.model.DiagnosticsDto
import com.example.starkfuturetest.data.model.MotorInfoDto
import com.example.starkfuturetest.data.model.RideSettingsDto
import com.example.starkfuturetest.data.model.SessionInfoDto
import com.example.starkfuturetest.data.model.TelemetrySnapshotDto
import com.example.starkfuturetest.domain.model.BatteryInfo
import com.example.starkfuturetest.domain.model.BikeInfo
import com.example.starkfuturetest.domain.model.DiagnosticWarning
import com.example.starkfuturetest.domain.model.Diagnostics
import com.example.starkfuturetest.domain.model.MotorInfo
import com.example.starkfuturetest.domain.model.RideSettings
import com.example.starkfuturetest.domain.model.SessionInfo
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.model.WarningSeverity
import javax.inject.Inject

class TelemetryDtoToDomainMapperImpl @Inject constructor() : TelemetryDtoToDomainMapper {

    override fun map(dto: TelemetrySnapshotDto): TelemetrySnapshot = TelemetrySnapshot(
        bike = dto.bike.toDomain(),
        timestamp = dto.timestamp,
        battery = dto.battery.toDomain(),
        motor = dto.motor.toDomain(),
        rideSettings = dto.rideSettings.toDomain(),
        session = dto.session.toDomain(),
        diagnostics = dto.diagnostics.toDomain(),
    )

    private fun BikeInfoDto.toDomain() = BikeInfo(
        model = model, variant = variant, firmwareVersion = firmwareVersion, imageUrl = imageUrl,
    )

    private fun BatteryInfoDto.toDomain() = BatteryInfo(
        stateOfChargePct = stateOfChargePct,
        estimatedRangeKm = estimatedRangeKm,
        temperatureC = temperatureC,
        chargingState = chargingState,
    )

    private fun MotorInfoDto.toDomain() = MotorInfo(powerHp = powerHp, temperatureC = temperatureC)

    private fun RideSettingsDto.toDomain() = RideSettings(
        powerMap = powerMap,
        maxPowerHp = maxPowerHp,
        engineBrakingPct = engineBrakingPct,
        regenPct = regenPct,
    )

    private fun SessionInfoDto.toDomain() = SessionInfo(
        durationS = durationS, distanceKm = distanceKm, maxSpeedKmh = maxSpeedKmh,
    )

    private fun DiagnosticsDto.toDomain() = Diagnostics(
        faultCodes = faultCodes,
        warnings = warnings.map { it.toDomain() },
    )

    private fun DiagnosticWarningDto.toDomain() = DiagnosticWarning(
        code = code,
        message = message,
        severity = when (severity.lowercase()) {
            "warning" -> WarningSeverity.Warning
            "critical" -> WarningSeverity.Critical
            "info" -> WarningSeverity.Info
            else -> WarningSeverity.Unknown
        },
    )
}