package com.example.starkfuturetest.domain.model

data class BatteryInfo(
    val stateOfChargePct: Int,
    val estimatedRangeKm: Int,
    val temperatureC: Double,
    val chargingState: String,
)