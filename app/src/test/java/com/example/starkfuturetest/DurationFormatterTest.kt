package com.example.starkfuturetest

import com.example.starkfuturetest.presentation.dashboard.formatter.DurationFormatterImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DurationFormatterTest {

    private lateinit var formatter: DurationFormatterImpl

    @Before
    fun setUp() {
        formatter = DurationFormatterImpl()
    }

    @Test
    fun `3742 seconds formats to 1h 02m`() {
        assertEquals("1h 02m", formatter.format(3742))
    }

    @Test
    fun `3600 seconds formats to 1h 00m`() {
        assertEquals("1h 00m", formatter.format(3600))
    }

    @Test
    fun `60 seconds formats to 1m`() {
        assertEquals("1m", formatter.format(60))
    }

    @Test
    fun `0 seconds formats to 0m`() {
        assertEquals("0m", formatter.format(0))
    }

    @Test
    fun `7322 seconds formats to 2h 02m`() {
        assertEquals("2h 02m", formatter.format(7322))
    }

    @Test
    fun `90 seconds formats to 1m`() {
        assertEquals("1m", formatter.format(90))
    }

    @Test
    fun `7200 seconds formats to 2h 00m`() {
        assertEquals("2h 00m", formatter.format(7200))
    }
}