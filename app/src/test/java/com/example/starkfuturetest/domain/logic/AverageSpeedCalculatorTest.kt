package com.example.starkfuturetest.domain.logic

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AverageSpeedCalculatorTest {

    private lateinit var calculator: AverageSpeedCalculatorImpl

    @Before
    fun setUp() {
        calculator = AverageSpeedCalculatorImpl()
    }

    @Test
    fun `normal session calculates correct average speed`() {
        assertEquals(23.77, calculator.calculate(distanceKm = 24.7, durationS = 3742), 0.01)
    }

    @Test
    fun `exactly one hour at 50 kmh`() {
        assertEquals(50.0, calculator.calculate(distanceKm = 50.0, durationS = 3600), 0.001)
    }

    @Test
    fun `zero duration returns 0`() {
        assertEquals(0.0, calculator.calculate(distanceKm = 10.0, durationS = 0), 0.001)
    }

    @Test
    fun `negative duration returns 0`() {
        assertEquals(0.0, calculator.calculate(distanceKm = 10.0, durationS = -1), 0.001)
    }

    @Test
    fun `zero distance returns 0`() {
        assertEquals(0.0, calculator.calculate(distanceKm = 0.0, durationS = 3600), 0.001)
    }
}