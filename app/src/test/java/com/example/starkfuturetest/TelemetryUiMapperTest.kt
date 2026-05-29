package com.example.starkfuturetest

import com.example.starkfuturetest.core.resources.ResourcesRepository
import com.example.starkfuturetest.domain.logic.AverageSpeedCalculatorImpl
import com.example.starkfuturetest.domain.logic.BatteryStatusResolverImpl
import com.example.starkfuturetest.domain.model.BatteryInfo
import com.example.starkfuturetest.domain.model.BatteryStatus
import com.example.starkfuturetest.domain.model.BikeInfo
import com.example.starkfuturetest.domain.model.DiagnosticWarning
import com.example.starkfuturetest.domain.model.Diagnostics
import com.example.starkfuturetest.domain.model.MotorInfo
import com.example.starkfuturetest.domain.model.RideSettings
import com.example.starkfuturetest.domain.model.SessionInfo
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.model.WarningSeverity
import com.example.starkfuturetest.presentation.dashboard.formatter.DurationFormatterImpl
import com.example.starkfuturetest.presentation.dashboard.formatter.TelemetryValueFormatterImpl
import com.example.starkfuturetest.presentation.dashboard.mapper.TelemetryUiMapperImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TelemetryUiMapperTest {

    private lateinit var mapper: TelemetryUiMapperImpl

    private val snapshot = TelemetrySnapshot(
        bike = BikeInfo(
            model = "Stark VARG MX 1.2",
            variant = "Alpha",
            firmwareVersion = "3.4.1",
            imageUrl = "https://example.com/bike.jpg",
        ),
        timestamp = "2024-03-15T10:30:00Z",
        battery = BatteryInfo(
            stateOfChargePct = 73,
            estimatedRangeKm = 38,
            temperatureC = 34.7,
            chargingState = "discharging",
        ),
        motor = MotorInfo(powerHp = 52.4, temperatureC = 61.2),
        rideSettings = RideSettings(
            powerMap = "enduro",
            maxPowerHp = 80,
            engineBrakingPct = 45,
            regenPct = 60,
        ),
        session = SessionInfo(durationS = 3742, distanceKm = 24.7, maxSpeedKmh = 94.1),
        diagnostics = Diagnostics(
            faultCodes = emptyList(),
            warnings = listOf(
                DiagnosticWarning(
                    code = "W_MOT_TEMP_HIGH",
                    message = "Motor temperature high",
                    severity = WarningSeverity.Warning,
                ),
            ),
        ),
    )

    @Before
    fun setUp() {
        val resources = mockk<ResourcesRepository>()

        every { resources.getString(R.string.format_duration_hm, any(), any()) } answers {
            "${secondArg<Int>()}h ${thirdArg<String>()}m"
        }
        every { resources.getString(R.string.format_duration_m, any()) } answers {
            "${secondArg<Int>()}m"
        }
        every { resources.getString(R.string.format_speed_kmh, any()) } answers {
            "%.1f km/h".format(secondArg<Double>())
        }
        every { resources.getString(R.string.format_power_hp, any()) } answers {
            "%.1f hp".format(secondArg<Double>())
        }
        every { resources.getString(R.string.format_temperature_c, any()) } answers {
            "%.1f°C".format(secondArg<Double>())
        }
        every { resources.getString(R.string.format_distance_km, any()) } answers {
            "%.1f km".format(secondArg<Double>())
        }
        every { resources.getString(R.string.format_range_km, any()) } answers {
            "${secondArg<Int>()} km"
        }
        every { resources.getString(R.string.format_percentage, any()) } answers {
            "${secondArg<Int>()}%"
        }

        mapper = TelemetryUiMapperImpl(
            batteryStatusResolver = BatteryStatusResolverImpl(),
            averageSpeedCalculator = AverageSpeedCalculatorImpl(),
            durationFormatter = DurationFormatterImpl(resources),
            valueFormatter = TelemetryValueFormatterImpl(resources),
        )
    }

    @Test
    fun `maps bike fields correctly`() {
        val result = mapper.map(snapshot)
        assertEquals("Stark VARG MX 1.2", result.bikeModel)
        assertEquals("Alpha", result.variant)
        assertEquals("3.4.1", result.firmwareVersion)
    }

    @Test
    fun `formats timestamp correctly`() {
        val result = mapper.map(snapshot)
        assertEquals("2024-03-15 10:30", result.formattedTimestamp)
    }

    @Test
    fun `battery percentage and status`() {
        val result = mapper.map(snapshot)
        assertEquals(73, result.battery.stateOfChargePct)
        assertEquals("73%", result.battery.displayCharge)
        assertEquals(BatteryStatus.Healthy, result.battery.batteryStatus)
    }

    @Test
    fun `battery charging state is capitalized`() {
        val result = mapper.map(snapshot)
        assertEquals("Discharging", result.battery.chargingState)
    }

    @Test
    fun `battery estimated range formatted`() {
        val result = mapper.map(snapshot)
        assertEquals("38 km", result.battery.estimatedRange)
    }

    @Test
    fun `motor power formatted`() {
        val result = mapper.map(snapshot)
        assertEquals("52.4 hp", result.motor.power)
    }

    @Test
    fun `power map is capitalized`() {
        val result = mapper.map(snapshot)
        assertEquals("Enduro", result.rideSettings.powerMap)
    }

    @Test
    fun `session duration formatted`() {
        val result = mapper.map(snapshot)
        assertEquals("1h 02m", result.session.duration)
    }

    @Test
    fun `session max speed formatted`() {
        val result = mapper.map(snapshot)
        assertEquals("94.1 km/h", result.session.maxSpeed)
    }

    @Test
    fun `session average speed calculated and formatted`() {
        val result = mapper.map(snapshot)
        assertEquals("23.8 km/h", result.session.averageSpeed)
    }

    @Test
    fun `warnings mapped correctly`() {
        val result = mapper.map(snapshot)
        assertEquals(1, result.warnings.size)
        assertEquals("W_MOT_TEMP_HIGH", result.warnings[0].code)
        assertEquals("Motor temperature high", result.warnings[0].message)
    }
}