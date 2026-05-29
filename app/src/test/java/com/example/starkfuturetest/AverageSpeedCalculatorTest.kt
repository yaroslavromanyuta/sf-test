package com.example.starkfuturetest

import com.example.starkfuturetest.domain.logic.AverageSpeedCalculatorImpl
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
        val result = calculator.calculate(distanceKm = 24.7, durationS = 3742)
        assertEquals(23.77, result, 0.01)
    }

    @Test
    fun `zero duration returns 0`() {
        val result = calculator.calculate(distanceKm = 10.0, durationS = 0)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `negative duration returns 0`() {
        val result = calculator.calculate(distanceKm = 10.0, durationS = -1)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `zero distance returns 0`() {
        val result = calculator.calculate(distanceKm = 0.0, durationS = 3600)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `exactly one hour at 50kmh`() {
        val result = calculator.calculate(distanceKm = 50.0, durationS = 3600)
        assertEquals(50.0, result, 0.001)
    }
}