package com.example.starkfuturetest.domain.usecase

import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.data.repository.TelemetryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTelemetrySnapshotUseCaseImpl @Inject constructor(
    private val repository: TelemetryRepository,
) : GetTelemetrySnapshotUseCase {
    override operator fun invoke(): Flow<AppResult<TelemetrySnapshot>> =
        repository.getTelemetrySnapshotFlow()
}