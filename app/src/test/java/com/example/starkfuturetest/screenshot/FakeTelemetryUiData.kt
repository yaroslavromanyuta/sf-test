package com.example.starkfuturetest.screenshot

import com.example.starkfuturetest.domain.model.BatteryStatus
import com.example.starkfuturetest.presentation.dashboard.BatteryUiModel
import com.example.starkfuturetest.presentation.dashboard.MotorUiModel
import com.example.starkfuturetest.presentation.dashboard.RideSettingsUiModel
import com.example.starkfuturetest.presentation.dashboard.SessionUiModel
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiModel
import com.example.starkfuturetest.presentation.dashboard.WarningUiModel
import kotlinx.collections.immutable.persistentListOf

internal val fakeDashboardUiModel = TelemetryDashboardUiModel(
    bikeModel = "Stark VARG MX 1.2",
    variant = "Alpha",
    firmwareVersion = "3.4.1",
    formattedTimestamp = "2025-05-19 10:32",
    imageUrl = "",
    battery = BatteryUiModel(
        stateOfChargePct = 73,
        displayCharge = "73%",
        chargingState = "Discharging",
        estimatedRange = "38 km",
        temperatureC = "34.7°C",
        batteryStatus = BatteryStatus.Healthy,
    ),
    motor = MotorUiModel(power = "52.4 hp", temperatureC = "61.2°C"),
    rideSettings = RideSettingsUiModel(
        powerMap = "Enduro",
        maxPower = "80.0 hp",
        engineBraking = "45%",
        regen = "60%",
    ),
    session = SessionUiModel(
        duration = "1h 02m",
        distance = "24.7 km",
        maxSpeed = "94.1 km/h",
        averageSpeed = "23.8 km/h",
    ),
    warnings = persistentListOf(
        WarningUiModel("W_MOT_TEMP_HIGH", "Motor temperature elevated", "Warning"),
    ),
    faultCodes = persistentListOf("E_SENS_THROTTLE_OOR"),
)

internal val fakeDashboardUiModelCritical = fakeDashboardUiModel.copy(
    battery = fakeDashboardUiModel.battery.copy(
        stateOfChargePct = 8,
        displayCharge = "8%",
        batteryStatus = BatteryStatus.Critical,
        estimatedRange = "3 km",
    ),
    warnings = persistentListOf(
        WarningUiModel("W_BATT_CRITICAL", "Battery critically low", "Critical"),
        WarningUiModel("W_MOT_TEMP_HIGH", "Motor temperature elevated", "Warning"),
    ),
    faultCodes = persistentListOf("E_SENS_THROTTLE_OOR", "E_CAN_BUS_TIMEOUT"),
)