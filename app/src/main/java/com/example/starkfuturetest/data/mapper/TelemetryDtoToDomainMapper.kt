package com.example.starkfuturetest.data.mapper

import com.example.starkfuturetest.data.model.TelemetrySnapshotDto
import com.example.starkfuturetest.domain.model.TelemetrySnapshot

interface TelemetryDtoToDomainMapper {
    fun map(dto: TelemetrySnapshotDto): TelemetrySnapshot
}