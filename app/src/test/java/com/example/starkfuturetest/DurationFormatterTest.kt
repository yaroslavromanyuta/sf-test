package com.example.starkfuturetest

import com.example.starkfuturetest.core.resources.ResourcesRepository
import com.example.starkfuturetest.presentation.dashboard.formatter.DurationFormatterImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DurationFormatterTest {

    private lateinit var formatter: DurationFormatterImpl

    @Before
    fun setUp() {
        val resources = mockk<ResourcesRepository>()
        every { resources.getString(R.string.format_duration_hm, any(), any()) } answers {
            val hours = secondArg<Int>()
            val minutesPadded = thirdArg<String>()
            "${hours}h ${minutesPadded}m"
        }
        every { resources.getString(R.string.format_duration_m, any()) } answers {
            val minutes = secondArg<Int>()
            "${minutes}m"
        }
        formatter = DurationFormatterImpl(resources)
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
    fun `7200 seconds formats to 2h 00m`() {
        assertEquals("2h 00m", formatter.format(7200))
    }
}