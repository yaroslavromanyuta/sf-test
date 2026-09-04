package com.example.starkfuturetest.presentation.dashboard.formatter

import com.example.starkfuturetest.R
import com.example.starkfuturetest.core.resources.ResourcesRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TelemetryValueFormatterImplTest {

    private lateinit var formatter: TelemetryValueFormatterImpl

    @Before
    fun setUp() {
        val resources = mockk<ResourcesRepository>()
        every { resources.getString(R.string.format_speed_kmh, any()) } answers {
            "%.1f km/h".format((invocation.args[1] as Array<*>)[0] as Double)
        }
        every { resources.getString(R.string.format_power_hp, any()) } answers {
            "%.1f hp".format((invocation.args[1] as Array<*>)[0] as Double)
        }
        every { resources.getString(R.string.format_temperature_c, any()) } answers {
            "%.1f°C".format((invocation.args[1] as Array<*>)[0] as Double)
        }
        every { resources.getString(R.string.format_distance_km, any()) } answers {
            "%.1f km".format((invocation.args[1] as Array<*>)[0] as Double)
        }
        every { resources.getString(R.string.format_range_km, any()) } answers {
            "${(invocation.args[1] as Array<*>)[0] as Int} km"
        }
        every { resources.getString(R.string.format_percentage, any()) } answers {
            "${(invocation.args[1] as Array<*>)[0] as Int}%"
        }
        formatter = TelemetryValueFormatterImpl(resources)
    }

    @Test
    fun `formatTimestamp extracts date and time from ISO string`() {
        assertEquals("2024-03-15 10:30", formatter.formatTimestamp("2024-03-15T10:30:00Z"))
    }

    @Test
    fun `formatTimestamp works with different datetime`() {
        assertEquals("2025-05-19 14:57", formatter.formatTimestamp("2025-05-19T14:57:08Z"))
    }

    @Test
    fun `formatTimestamp returns original on unexpected input`() {
        val short = "bad"
        assertEquals(short, formatter.formatTimestamp(short))
    }

    @Test
    fun `formatPowerMap capitalises first character`() {
        assertEquals("Enduro", formatter.formatPowerMap("enduro"))
    }

    @Test
    fun `formatPowerMap capitalises track`() {
        assertEquals("Track", formatter.formatPowerMap("track"))
    }

    @Test
    fun `formatPowerMap already uppercase is unchanged`() {
        assertEquals("Enduro", formatter.formatPowerMap("Enduro"))
    }

    @Test
    fun `formatSpeed formats with one decimal and unit`() {
        assertEquals("94.1 km/h", formatter.formatSpeed(94.1))
    }

    @Test
    fun `formatPower formats with one decimal and unit`() {
        assertEquals("52.4 hp", formatter.formatPower(52.4))
    }

    @Test
    fun `formatTemperature formats with one decimal and degree symbol`() {
        assertEquals("34.7°C", formatter.formatTemperature(34.7))
    }

    @Test
    fun `formatDistance formats with one decimal and unit`() {
        assertEquals("24.7 km", formatter.formatDistance(24.7))
    }

    @Test
    fun `formatRange formats integer without decimal`() {
        assertEquals("38 km", formatter.formatRange(38))
    }

    @Test
    fun `formatPercentage formats integer with percent sign`() {
        assertEquals("73%", formatter.formatPercentage(73))
    }

    @Test
    fun `formatPercentage formats zero`() {
        assertEquals("0%", formatter.formatPercentage(0))
    }
}