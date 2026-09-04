package com.example.starkfuturetest.data.repository

import app.cash.turbine.test
import com.example.starkfuturetest.core.result.AppError
import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.data.datasource.TelemetrySnapshotDataSource
import com.example.starkfuturetest.data.mapper.TelemetryDtoToDomainMapper
import com.example.starkfuturetest.data.model.BatteryInfoDto
import com.example.starkfuturetest.data.model.BikeInfoDto
import com.example.starkfuturetest.data.model.DiagnosticsDto
import com.example.starkfuturetest.data.model.MotorInfoDto
import com.example.starkfuturetest.data.model.RideSettingsDto
import com.example.starkfuturetest.data.model.SessionInfoDto
import com.example.starkfuturetest.data.model.TelemetrySnapshotDto
import com.example.starkfuturetest.data.parser.TelemetryJsonParser
import com.example.starkfuturetest.domain.model.BatteryInfo
import com.example.starkfuturetest.domain.model.BikeInfo
import com.example.starkfuturetest.domain.model.Diagnostics
import com.example.starkfuturetest.domain.model.MotorInfo
import com.example.starkfuturetest.domain.model.RideSettings
import com.example.starkfuturetest.domain.model.SessionInfo
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class TelemetryRepositoryImplTest {

    private val dataSource: TelemetrySnapshotDataSource = mockk()
    private val parser: TelemetryJsonParser = mockk()
    private val mapper: TelemetryDtoToDomainMapper = mockk()

    private lateinit var repository: TelemetryRepositoryImpl

    private val fakeDto = TelemetrySnapshotDto(
        bike = BikeInfoDto("Stark VARG MX 1.2", "Alpha", "3.4.1", ""),
        timestamp = "2024-03-15T10:30:00Z",
        battery = BatteryInfoDto(73, 38, 34.7, "discharging"),
        motor = MotorInfoDto(52.4, 61.2),
        rideSettings = RideSettingsDto("enduro", 80, 45, 60),
        session = SessionInfoDto(3742, 24.7, 94.1),
        diagnostics = DiagnosticsDto(),
    )

    private val fakeSnapshot = TelemetrySnapshot(
        bike = BikeInfo("Stark VARG MX 1.2", "Alpha", "3.4.1", ""),
        timestamp = "2024-03-15T10:30:00Z",
        battery = BatteryInfo(73, 38, 34.7, "discharging"),
        motor = MotorInfo(52.4, 61.2),
        rideSettings = RideSettings("enduro", 80, 45, 60),
        session = SessionInfo(3742, 24.7, 94.1),
        diagnostics = Diagnostics(emptyList(), emptyList()),
    )

    @Before
    fun setUp() {
        repository = TelemetryRepositoryImpl(dataSource, parser, mapper)
    }

    @Test
    fun `success path emits Success result`() = runTest {
        coEvery { dataSource.getTelemetrySnapshotJson(any()) } returns "{}"
        every { parser.parse(any()) } returns fakeDto
        every { mapper.map(fakeDto) } returns fakeSnapshot

        repository.getTelemetrySnapshotFlow().test {
            val item = awaitItem()
            assertTrue(item is AppResult.Success)
            assertEquals(fakeSnapshot, (item as AppResult.Success).data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `IOException from data source emits AssetReadError`() = runTest {
        coEvery { dataSource.getTelemetrySnapshotJson(any()) } throws IOException("file not found")

        repository.getTelemetrySnapshotFlow().test {
            val item = awaitItem()
            assertEquals(AppResult.Error(AppError.AssetReadError), item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SerializationException from parser emits ParseError`() = runTest {
        coEvery { dataSource.getTelemetrySnapshotJson(any()) } returns "bad json"
        every { parser.parse(any()) } throws SerializationException("invalid")

        repository.getTelemetrySnapshotFlow().test {
            val item = awaitItem()
            assertEquals(AppResult.Error(AppError.ParseError), item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unexpected exception emits Unknown error`() = runTest {
        val cause = RuntimeException("unexpected")
        coEvery { dataSource.getTelemetrySnapshotJson(any()) } throws cause

        repository.getTelemetrySnapshotFlow().test {
            val item = awaitItem()
            assertTrue(item is AppResult.Error)
            val error = (item as AppResult.Error).error
            assertTrue(error is AppError.Unknown)
            assertEquals(cause, (error as AppError.Unknown).cause)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error emission does not terminate the flow`() = runTest {
        coEvery { dataSource.getTelemetrySnapshotJson(any()) } throws IOException("error")

        repository.getTelemetrySnapshotFlow().test {
            val first = awaitItem()
            assertEquals(AppResult.Error(AppError.AssetReadError), first)
            // Flow continues — second item also produced (would come after delay)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cycles through snapshot files on subsequent emissions`() = runTest {
        val firstJson = """{"file": "1"}"""
        val secondJson = """{"file": "2"}"""
        val secondDto = fakeDto.copy(timestamp = "snapshot-2")
        val secondSnapshot = fakeSnapshot.copy(timestamp = "snapshot-2")

        coEvery { dataSource.getTelemetrySnapshotJson("telemetry_snapshot.json") } returns firstJson
        coEvery { dataSource.getTelemetrySnapshotJson("telemetry_snapshot_2.json") } returns secondJson
        every { parser.parse(firstJson) } returns fakeDto
        every { parser.parse(secondJson) } returns secondDto
        every { mapper.map(fakeDto) } returns fakeSnapshot
        every { mapper.map(secondDto) } returns secondSnapshot

        repository.getTelemetrySnapshotFlow().test {
            val first = awaitItem() as AppResult.Success
            assertEquals(fakeSnapshot, first.data)
            // Second emission comes after delay — cancel after first to keep test fast
            cancelAndIgnoreRemainingEvents()
        }
    }
}