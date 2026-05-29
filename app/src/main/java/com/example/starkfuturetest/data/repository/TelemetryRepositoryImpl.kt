package com.example.starkfuturetest.data.repository

import com.example.starkfuturetest.core.result.AppError
import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.data.datasource.TelemetrySnapshotDataSource
import com.example.starkfuturetest.data.mapper.TelemetryDtoToDomainMapper
import com.example.starkfuturetest.data.parser.TelemetryJsonParser
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.repository.TelemetryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import java.io.IOException
import javax.inject.Inject

class TelemetryRepositoryImpl @Inject constructor(
    private val dataSource: TelemetrySnapshotDataSource,
    private val parser: TelemetryJsonParser,
    private val mapper: TelemetryDtoToDomainMapper,
) : TelemetryRepository {

    override suspend fun getTelemetrySnapshot(): AppResult<TelemetrySnapshot> =
        try {
            val json = dataSource.getTelemetrySnapshotJson()
            val dto = parser.parse(json)
            AppResult.Success(mapper.map(dto))
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {
            AppResult.Error(AppError.AssetReadError)
        } catch (_: SerializationException) {
            AppResult.Error(AppError.ParseError)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e))
        }
}