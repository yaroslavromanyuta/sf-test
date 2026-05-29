package com.example.starkfuturetest.domain.usecase

import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.TelemetrySnapshot

interface GetTelemetrySnapshotUseCase {
    suspend operator fun invoke(): AppResult<TelemetrySnapshot>
}