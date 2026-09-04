package com.example.starkfuturetest.domain.logic

import com.example.starkfuturetest.domain.model.BatteryStatus

interface BatteryStatusResolver {
    fun resolve(stateOfChargePct: Int): BatteryStatus
}