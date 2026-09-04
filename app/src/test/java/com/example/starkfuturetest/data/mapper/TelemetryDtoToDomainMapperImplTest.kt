package com.example.starkfuturetest.data.mapper

import com.example.starkfuturetest.data.model.BatteryInfoDto
import com.example.starkfuturetest.data.model.BikeInfoDto
import com.example.starkfuturetest.data.model.DiagnosticWarningDto
import com.example.starkfuturetest.data.model.DiagnosticsDto
import com.example.starkfuturetest.data.model.MotorInfoDto
import com.example.starkfuturetest.data.model.RideSettingsDto
import com.example.starkfuturetest.data.model.SessionInfoDto
import com.example.starkfuturetest.data.model.TelemetrySnapshotDto
import com.example.starkfuturetest.domain.model.WarningSeverity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TelemetryDtoToDomainMapperImplTest {

    private lateinit var mapper: TelemetryDtoToDomainMapperImpl

    private val dto = TelemetrySnapshotDto(
        bike = BikeInfoDto("Stark VARG MX 1.2", "Alpha", "3.4.1", "https://img.example.com/bike.webp"),
        timestamp = "2024-03-15T10:30:00Z",
        battery = BatteryInfoDto(73, 38, 34.7, "discharging"),
        motor = MotorInfoDto(52.4, 61.2),
        rideSettings = RideSettingsDto("enduro", 80, 45, 60),
        session = SessionInfoDto(3742, 24.7, 94.1),
        diagnostics = DiagnosticsDto(
            faultCodes = listOf("E_SENS_THROTTLE_OOR"),
            warnings = listOf(DiagnosticWarningDto("W_MOT_TEMP_HIGH", "Motor temperature elevated", "warning")),
        ),
    )

    @Before
    fun setUp() {
        mapper = TelemetryDtoToDomainMapperImpl()
    }

    @Test
    fun `maps bike fields`() {
        val result = mapper.map(dto).bike
        assertEquals("Stark VARG MX 1.2", result.model)
        assertEquals("Alpha", result.variant)
        assertEquals("3.4.1", result.firmwareVersion)
        assertEquals("https://img.example.com/bike.webp", result.imageUrl)
    }

    @Test
    fun `maps timestamp`() {
        assertEquals("2024-03-15T10:30:00Z", mapper.map(dto).timestamp)
    }

    @Test
    fun `maps battery fields`() {
        val result = mapper.map(dto).battery
        assertEquals(73, result.stateOfChargePct)
        assertEquals(38, result.estimatedRangeKm)
        assertEquals(34.7, result.temperatureC, 0.001)
        assertEquals("discharging", result.chargingState)
    }

    @Test
    fun `maps motor fields`() {
        val result = mapper.map(dto).motor
        assertEquals(52.4, result.powerHp, 0.001)
        assertEquals(61.2, result.temperatureC, 0.001)
    }

    @Test
    fun `maps ride settings`() {
        val result = mapper.map(dto).rideSettings
        assertEquals("enduro", result.powerMap)
        assertEquals(80, result.maxPowerHp)
        assertEquals(45, result.engineBrakingPct)
        assertEquals(60, result.regenPct)
    }

    @Test
    fun `maps session fields`() {
        val result = mapper.map(dto).session
        assertEquals(3742, result.durationS)
        assertEquals(24.7, result.distanceKm, 0.001)
        assertEquals(94.1, result.maxSpeedKmh, 0.001)
    }

    @Test
    fun `maps fault codes`() {
        assertEquals(listOf("E_SENS_THROTTLE_OOR"), mapper.map(dto).diagnostics.faultCodes)
    }

    @Test
    fun `maps empty fault codes`() {
        val result = mapper.map(dto.copy(diagnostics = dto.diagnostics.copy(faultCodes = emptyList())))
        assertEquals(emptyList<String>(), result.diagnostics.faultCodes)
    }

    @Test
    fun `maps warning fields`() {
        val warning = mapper.map(dto).diagnostics.warnings[0]
        assertEquals("W_MOT_TEMP_HIGH", warning.code)
        assertEquals("Motor temperature elevated", warning.message)
    }

    @Test
    fun `maps severity warning`() {
        val result = mapWithSeverity("warning")
        assertEquals(WarningSeverity.Warning, result)
    }

    @Test
    fun `maps severity critical`() {
        val result = mapWithSeverity("critical")
        assertEquals(WarningSeverity.Critical, result)
    }

    @Test
    fun `maps severity info`() {
        val result = mapWithSeverity("info")
        assertEquals(WarningSeverity.Info, result)
    }

    @Test
    fun `unknown severity string maps to Unknown`() {
        val result = mapWithSeverity("error")
        assertEquals(WarningSeverity.Unknown, result)
    }

    @Test
    fun `severity mapping is case-insensitive`() {
        assertEquals(WarningSeverity.Warning, mapWithSeverity("WARNING"))
        assertEquals(WarningSeverity.Critical, mapWithSeverity("CRITICAL"))
        assertEquals(WarningSeverity.Info, mapWithSeverity("INFO"))
    }

    @Test
    fun `empty warnings maps to empty list`() {
        val result = mapper.map(dto.copy(diagnostics = dto.diagnostics.copy(warnings = emptyList())))
        assertEquals(0, result.diagnostics.warnings.size)
    }

    private fun mapWithSeverity(severity: String): WarningSeverity {
        val warning = DiagnosticWarningDto("CODE", "msg", severity)
        val dtoWithWarning = dto.copy(diagnostics = DiagnosticsDto(warnings = listOf(warning)))
        return mapper.map(dtoWithWarning).diagnostics.warnings[0].severity
    }
}