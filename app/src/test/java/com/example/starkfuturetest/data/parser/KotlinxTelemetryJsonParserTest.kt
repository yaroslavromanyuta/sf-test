package com.example.starkfuturetest.data.parser

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KotlinxTelemetryJsonParserTest {

    private lateinit var parser: KotlinxTelemetryJsonParser

    @Before
    fun setUp() {
        parser = KotlinxTelemetryJsonParser(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        )
    }

    @Test
    fun `parses valid JSON snapshot`() {
        val result = parser.parse(VALID_JSON)
        assertEquals("Stark VARG MX 1.2", result.bike.model)
        assertEquals("Alpha", result.bike.variant)
        assertEquals("3.4.1", result.bike.firmwareVersion)
        assertEquals("https://example.com/bike.jpg", result.bike.imageUrl)
        assertEquals("2024-03-15T10:30:00Z", result.timestamp)
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
    fun `parses fault codes`() {
        val result = parser.parse(VALID_JSON)
        assertEquals(listOf("E_SENS_THROTTLE_OOR"), result.diagnostics.faultCodes)
    }

    @Test
    fun `parses warnings`() {
        val result = parser.parse(VALID_JSON)
        assertEquals(1, result.diagnostics.warnings.size)
        assertEquals("W_MOT_TEMP_HIGH", result.diagnostics.warnings[0].code)
        assertEquals("Motor temperature elevated", result.diagnostics.warnings[0].message)
        assertEquals("warning", result.diagnostics.warnings[0].severity)
    }

    @Test
    fun `parses empty fault codes and warnings`() {
        val result = parser.parse(MINIMAL_JSON)
        assertEquals(emptyList<String>(), result.diagnostics.faultCodes)
        assertEquals(emptyList<String>(), result.diagnostics.warnings.map { it.code })
    }

    @Test(expected = SerializationException::class)
    fun `throws SerializationException on invalid JSON`() {
        parser.parse("{ not valid json }")
    }

    @Test(expected = SerializationException::class)
    fun `throws SerializationException on missing required fields`() {
        parser.parse("""{"bike": {}}""")
    }

    @Test
    fun `ignores unknown fields in JSON`() {
        val jsonWithExtra = VALID_JSON.replace(
            "\"model\": \"Stark VARG MX 1.2\"",
            "\"model\": \"Stark VARG MX 1.2\", \"unknown_field\": \"ignored\""
        )
        val result = parser.parse(jsonWithExtra)
        assertEquals("Stark VARG MX 1.2", result.bike.model)
    }

    companion object {
        private val VALID_JSON = """
            {
              "bike": {
                "model": "Stark VARG MX 1.2",
                "variant": "Alpha",
                "firmware_version": "3.4.1",
                "image_url": "https://example.com/bike.jpg"
              },
              "timestamp": "2024-03-15T10:30:00Z",
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
                "fault_codes": ["E_SENS_THROTTLE_OOR"],
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

        private val MINIMAL_JSON = """
            {
              "bike": {
                "model": "Stark VARG MX 1.2",
                "variant": "Alpha",
                "firmware_version": "3.4.1",
                "image_url": ""
              },
              "timestamp": "2024-03-15T10:30:00Z",
              "battery": {
                "state_of_charge_pct": 50,
                "estimated_range_km": 20,
                "temperature_c": 25.0,
                "charging_state": "discharging"
              },
              "motor": {
                "power_hp": 40.0,
                "temperature_c": 50.0
              },
              "ride_settings": {
                "power_map": "enduro",
                "max_power_hp": 80,
                "engine_braking_pct": 40,
                "regen_pct": 50
              },
              "session": {
                "duration_s": 1800,
                "distance_km": 12.0,
                "max_speed_kmh": 70.0
              },
              "diagnostics": {
                "fault_codes": [],
                "warnings": []
              }
            }
        """.trimIndent()
    }
}