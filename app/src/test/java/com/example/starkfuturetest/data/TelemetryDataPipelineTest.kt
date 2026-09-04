package com.example.starkfuturetest.data

import com.example.starkfuturetest.data.mapper.TelemetryDtoToDomainMapperImpl
import com.example.starkfuturetest.data.parser.KotlinxTelemetryJsonParser
import com.example.starkfuturetest.domain.model.WarningSeverity
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TelemetryDataPipelineTest {

    private lateinit var parser: KotlinxTelemetryJsonParser
    private lateinit var mapper: TelemetryDtoToDomainMapperImpl

    @Before
    fun setUp() {
        parser = KotlinxTelemetryJsonParser(Json { ignoreUnknownKeys = true; explicitNulls = false })
        mapper = TelemetryDtoToDomainMapperImpl()
    }

    private fun pipeline(json: String) = mapper.map(parser.parse(json))

    @Test
    fun `full JSON snapshot parses to correct domain model`() {
        val result = pipeline(SNAPSHOT_JSON)
        assertEquals("Stark VARG MX 1.2", result.bike.model)
        assertEquals("Alpha", result.bike.variant)
        assertEquals("3.4.1", result.bike.firmwareVersion)
        assertEquals("2025-05-19T10:32:45Z", result.timestamp)
        assertEquals(73, result.battery.stateOfChargePct)
        assertEquals(38, result.battery.estimatedRangeKm)
        assertEquals(34.7, result.battery.temperatureC, 0.001)
        assertEquals("discharging", result.battery.chargingState)
        assertEquals(52.4, result.motor.powerHp, 0.001)
        assertEquals(61.2, result.motor.temperatureC, 0.001)
        assertEquals("enduro", result.rideSettings.powerMap)
        assertEquals(80, result.rideSettings.maxPowerHp)
        assertEquals(45, result.rideSettings.engineBrakingPct)
        assertEquals(60, result.rideSettings.regenPct)
        assertEquals(3742, result.session.durationS)
        assertEquals(24.7, result.session.distanceKm, 0.001)
        assertEquals(94.1, result.session.maxSpeedKmh, 0.001)
    }

    @Test
    fun `warning is mapped with correct fields and severity`() {
        val result = pipeline(SNAPSHOT_JSON)
        assertEquals(1, result.diagnostics.warnings.size)
        val warning = result.diagnostics.warnings[0]
        assertEquals("W_MOT_TEMP_HIGH", warning.code)
        assertEquals("Motor temperature elevated", warning.message)
        assertEquals(WarningSeverity.Warning, warning.severity)
    }

    @Test
    fun `fault codes are mapped`() {
        val withFaults = SNAPSHOT_JSON.replace(
            """"fault_codes": []""",
            """"fault_codes": ["E_SENS_THROTTLE_OOR", "E_CAN_BUS_TIMEOUT"]""",
        )
        val result = pipeline(withFaults)
        assertEquals(listOf("E_SENS_THROTTLE_OOR", "E_CAN_BUS_TIMEOUT"), result.diagnostics.faultCodes)
    }

    @Test
    fun `empty fault codes produce empty list`() {
        assertEquals(emptyList<String>(), pipeline(SNAPSHOT_JSON).diagnostics.faultCodes)
    }

    @Test
    fun `multiple warnings are all mapped`() {
        val result = pipeline(TWO_WARNINGS_JSON)
        assertEquals(2, result.diagnostics.warnings.size)
        assertEquals(WarningSeverity.Warning, result.diagnostics.warnings[0].severity)
        assertEquals(WarningSeverity.Critical, result.diagnostics.warnings[1].severity)
    }

    companion object {
        private val TWO_WARNINGS_JSON = """
            {
              "bike": {"model":"Stark VARG MX 1.2","variant":"Alpha","firmware_version":"3.4.1","image_url":""},
              "timestamp": "2025-05-19T10:32:45Z",
              "battery": {"state_of_charge_pct":73,"estimated_range_km":38,"temperature_c":34.7,"charging_state":"discharging"},
              "motor": {"power_hp":52.4,"temperature_c":61.2},
              "ride_settings": {"power_map":"enduro","max_power_hp":80,"engine_braking_pct":45,"regen_pct":60},
              "session": {"duration_s":3742,"distance_km":24.7,"max_speed_kmh":94.1},
              "diagnostics": {
                "fault_codes": [],
                "warnings": [
                  {"code":"W_MOT_TEMP_HIGH","message":"Motor temperature elevated","severity":"warning"},
                  {"code":"W_BATT_CRITICAL","message":"Battery critically low","severity":"critical"}
                ]
              }
            }
        """.trimIndent()

        private val SNAPSHOT_JSON = """
            {
              "bike": {
                "model": "Stark VARG MX 1.2",
                "variant": "Alpha",
                "firmware_version": "3.4.1",
                "image_url": "https://assets.starkfuture.com/bike.webp"
              },
              "timestamp": "2025-05-19T10:32:45Z",
              "battery": {
                "state_of_charge_pct": 73,
                "estimated_range_km": 38,
                "temperature_c": 34.7,
                "charging_state": "discharging"
              },
              "motor": {
                "power_hp": 52.4,
                "temperature_c": 61.2
              },
              "ride_settings": {
                "power_map": "enduro",
                "max_power_hp": 80,
                "engine_braking_pct": 45,
                "regen_pct": 60
              },
              "session": {
                "duration_s": 3742,
                "distance_km": 24.7,
                "max_speed_kmh": 94.1
              },
              "diagnostics": {
                "fault_codes": [],
                "warnings": [
                  {
                    "code": "W_MOT_TEMP_HIGH",
                    "message": "Motor temperature elevated",
                    "severity": "warning"
                  }
                ]
              }
            }
        """.trimIndent()
    }
}