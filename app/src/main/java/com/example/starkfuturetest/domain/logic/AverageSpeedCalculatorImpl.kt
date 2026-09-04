package com.example.starkfuturetest.domain.logic

import javax.inject.Inject

class AverageSpeedCalculatorImpl @Inject constructor() : AverageSpeedCalculator {
    override fun calculate(distanceKm: Double, durationS: Int): Double {
        if (durationS <= 0) return 0.0
        return distanceKm / (durationS / 3600.0)
    }
}