package com.example.starkfuturetest.data.parser

import com.example.starkfuturetest.data.model.TelemetrySnapshotDto
import kotlinx.serialization.json.Json
import javax.inject.Inject

class KotlinxTelemetryJsonParser @Inject constructor(
    private val json: Json,
) : TelemetryJsonParser {
    override fun parse(jsonString: String): TelemetrySnapshotDto =
        json.decodeFromString(jsonString)
}