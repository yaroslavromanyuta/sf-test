package com.example.starkfuturetest.domain.usecase

import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import kotlinx.coroutines.flow.Flow

interface GetTelemetrySnapshotUseCase {
    operator fun invoke(): Flow<AppResult<TelemetrySnapshot>>
}