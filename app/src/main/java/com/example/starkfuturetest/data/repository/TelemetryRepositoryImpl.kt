package com.example.starkfuturetest.data.repository

import com.example.starkfuturetest.core.result.AppError
import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.data.datasource.TelemetrySnapshotDataSource
import com.example.starkfuturetest.data.mapper.TelemetryDtoToDomainMapper
import com.example.starkfuturetest.data.parser.TelemetryJsonParser
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.repository.TelemetryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import java.io.IOException
import javax.inject.Inject

private val SNAPSHOT_FILES = listOf(
    "telemetry_snapshot.json",
    "telemetry_snapshot_2.json",
    "telemetry_snapshot_3.json",
)

private const val UPDATE_INTERVAL_MS = 15_000L

class TelemetryRepositoryImpl @Inject constructor(
    private val dataSource: TelemetrySnapshotDataSource,
    private val parser: TelemetryJsonParser,
    private val mapper: TelemetryDtoToDomainMapper,
) : TelemetryRepository {

    override fun getTelemetrySnapshotFlow(): Flow<AppResult<TelemetrySnapshot>> = flow {
        var index = 0
        while (true) {
            val result = try {
                val json = dataSource.getTelemetrySnapshotJson(SNAPSHOT_FILES[index])
                AppResult.Success(mapper.map(parser.parse(json)))
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                AppResult.Error(AppError.AssetReadError)
            } catch (_: SerializationException) {
                AppResult.Error(AppError.ParseError)
            } catch (e: Exception) {
                AppResult.Error(AppError.Unknown(e))
            }
            emit(result)
            index = (index + 1) % SNAPSHOT_FILES.size
            delay(UPDATE_INTERVAL_MS)
        }
    }
}