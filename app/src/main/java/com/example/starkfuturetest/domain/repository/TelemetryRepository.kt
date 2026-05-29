package com.example.starkfuturetest.domain.repository

import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import kotlinx.coroutines.flow.Flow

interface TelemetryRepository {
    fun getTelemetrySnapshotFlow(): Flow<AppResult<TelemetrySnapshot>>
}