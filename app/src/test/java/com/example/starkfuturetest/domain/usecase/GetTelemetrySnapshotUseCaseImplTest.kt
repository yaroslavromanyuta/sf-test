package com.example.starkfuturetest.domain.usecase

import com.example.starkfuturetest.core.result.AppError
import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.BatteryInfo
import com.example.starkfuturetest.domain.model.BikeInfo
import com.example.starkfuturetest.domain.model.Diagnostics
import com.example.starkfuturetest.domain.model.MotorInfo
import com.example.starkfuturetest.domain.model.RideSettings
import com.example.starkfuturetest.domain.model.SessionInfo
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.repository.TelemetryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTelemetrySnapshotUseCaseImplTest {

    private val repository: TelemetryRepository = mockk()
    private lateinit var useCase: GetTelemetrySnapshotUseCaseImpl

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
        useCase = GetTelemetrySnapshotUseCaseImpl(repository)
    }

    @Test
    fun `delegates to repository flow`() = runTest {
        every { repository.getTelemetrySnapshotFlow() } returns flowOf(AppResult.Success(fakeSnapshot))

        val results = useCase().toList()

        verify(exactly = 1) { repository.getTelemetrySnapshotFlow() }
        assertEquals(1, results.size)
        assertEquals(AppResult.Success(fakeSnapshot), results[0])
    }

    @Test
    fun `propagates error from repository`() = runTest {
        every { repository.getTelemetrySnapshotFlow() } returns flowOf(AppResult.Error(AppError.AssetReadError))

        val results = useCase().toList()

        assertEquals(1, results.size)
        assertEquals(AppResult.Error(AppError.AssetReadError), results[0])
    }

    @Test
    fun `propagates multiple emissions from repository`() = runTest {
        val secondSnapshot = fakeSnapshot.copy(battery = fakeSnapshot.battery.copy(stateOfChargePct = 55))
        every { repository.getTelemetrySnapshotFlow() } returns flowOf(
            AppResult.Success(fakeSnapshot),
            AppResult.Success(secondSnapshot),
        )

        val results = useCase().toList()

        assertEquals(2, results.size)
        assertEquals(73, (results[0] as AppResult.Success).data.battery.stateOfChargePct)
        assertEquals(55, (results[1] as AppResult.Success).data.battery.stateOfChargePct)
    }
}