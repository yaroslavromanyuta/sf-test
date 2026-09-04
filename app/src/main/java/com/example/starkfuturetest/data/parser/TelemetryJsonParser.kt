package com.example.starkfuturetest.data.parser

import com.example.starkfuturetest.data.model.TelemetrySnapshotDto

interface TelemetryJsonParser {
    fun parse(json: String): TelemetrySnapshotDto
}