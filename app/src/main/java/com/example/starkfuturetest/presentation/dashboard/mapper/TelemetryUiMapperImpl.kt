package com.example.starkfuturetest.presentation.dashboard.mapper

import com.example.starkfuturetest.domain.logic.AverageSpeedCalculator
import com.example.starkfuturetest.domain.logic.BatteryStatusResolver
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.presentation.dashboard.BatteryUiModel
import com.example.starkfuturetest.presentation.dashboard.MotorUiModel
import com.example.starkfuturetest.presentation.dashboard.RideSettingsUiModel
import com.example.starkfuturetest.presentation.dashboard.SessionUiModel
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiModel
import com.example.starkfuturetest.presentation.dashboard.WarningUiModel
import com.example.starkfuturetest.presentation.dashboard.formatter.DurationFormatter
import com.example.starkfuturetest.presentation.dashboard.formatter.TelemetryValueFormatter
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class TelemetryUiMapperImpl @Inject constructor(
    private val batteryStatusResolver: BatteryStatusResolver,
    private val averageSpeedCalculator: AverageSpeedCalculator,
    private val durationFormatter: DurationFormatter,
    private val valueFormatter: TelemetryValueFormatter,
) : TelemetryUiMapper {

    override fun map(snapshot: TelemetrySnapshot): TelemetryDashboardUiModel {
        val batteryStatus = batteryStatusResolver.resolve(snapshot.battery.stateOfChargePct)
        val avgSpeed = averageSpeedCalculator.calculate(
            distanceKm = snapshot.session.distanceKm,
            durationS = snapshot.session.durationS,
        )
        return TelemetryDashboardUiModel(
            bikeModel = snapshot.bike.model,
            variant = snapshot.bike.variant,
            firmwareVersion = snapshot.bike.firmwareVersion,
            formattedTimestamp = valueFormatter.formatTimestamp(snapshot.timestamp),
            imageUrl = snapshot.bike.imageUrl,
            battery = BatteryUiModel(
                stateOfChargePct = snapshot.battery.stateOfChargePct,
                displayCharge = valueFormatter.formatPercentage(snapshot.battery.stateOfChargePct),
                chargingState = snapshot.battery.chargingState.replaceFirstChar { it.uppercaseChar() },
                estimatedRange = valueFormatter.formatRange(snapshot.battery.estimatedRangeKm),
                temperatureC = valueFormatter.formatTemperature(snapshot.battery.temperatureC),
                batteryStatus = batteryStatus,
            ),
            motor = MotorUiModel(
                power = valueFormatter.formatPower(snapshot.motor.powerHp),
                temperatureC = valueFormatter.formatTemperature(snapshot.motor.temperatureC),
            ),
            rideSettings = RideSettingsUiModel(
                powerMap = valueFormatter.formatPowerMap(snapshot.rideSettings.powerMap),
                maxPower = valueFormatter.formatPower(snapshot.rideSettings.maxPowerHp.toDouble()),
                engineBraking = valueFormatter.formatPercentage(snapshot.rideSettings.engineBrakingPct),
                regen = valueFormatter.formatPercentage(snapshot.rideSettings.regenPct),
            ),
            session = SessionUiModel(
                duration = durationFormatter.format(snapshot.session.durationS),
                distance = valueFormatter.formatDistance(snapshot.session.distanceKm),
                maxSpeed = valueFormatter.formatSpeed(snapshot.session.maxSpeedKmh),
                averageSpeed = valueFormatter.formatSpeed(avgSpeed),
            ),
            warnings = snapshot.diagnostics.warnings.map { warning ->
                WarningUiModel(
                    code = warning.code,
                    message = warning.message,
                    severity = warning.severity.name,
                )
            }.toImmutableList(),
            faultCodes = snapshot.diagnostics.faultCodes.toImmutableList(),
        )
    }
}