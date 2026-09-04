package com.example.starkfuturetest.domain.logic

interface AverageSpeedCalculator {
    fun calculate(distanceKm: Double, durationS: Int): Double
}