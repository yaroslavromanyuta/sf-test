package com.example.starkfuturetest.domain.repository

import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.TelemetrySnapshot

interface TelemetryRepository {
    suspend fun getTelemetrySnapshot(): AppResult<TelemetrySnapshot>
}