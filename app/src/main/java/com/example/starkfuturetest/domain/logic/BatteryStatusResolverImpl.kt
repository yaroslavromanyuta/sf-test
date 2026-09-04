package com.example.starkfuturetest.domain.logic

import com.example.starkfuturetest.domain.model.BatteryStatus
import javax.inject.Inject

class BatteryStatusResolverImpl @Inject constructor() : BatteryStatusResolver {
    override fun resolve(stateOfChargePct: Int): BatteryStatus = when (stateOfChargePct) {
        in 0..15 -> BatteryStatus.Critical
        in 16..49 -> BatteryStatus.Medium
        in 50..100 -> BatteryStatus.Healthy
        else -> BatteryStatus.Unknown
    }
}