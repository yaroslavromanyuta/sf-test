package com.example.starkfuturetest.domain.usecase

import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.repository.TelemetryRepository
import javax.inject.Inject

class GetTelemetrySnapshotUseCaseImpl @Inject constructor(
    private val repository: TelemetryRepository,
) : GetTelemetrySnapshotUseCase {
    override suspend operator fun invoke(): AppResult<TelemetrySnapshot> =
        repository.getTelemetrySnapshot()
}