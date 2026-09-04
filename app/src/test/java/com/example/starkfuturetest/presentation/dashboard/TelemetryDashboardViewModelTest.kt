package com.example.starkfuturetest.presentation.dashboard

import app.cash.turbine.test
import com.example.starkfuturetest.core.resources.ResourcesRepository
import com.example.starkfuturetest.core.result.AppError
import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.BatteryInfo
import com.example.starkfuturetest.domain.model.BatteryStatus
import com.example.starkfuturetest.domain.model.BikeInfo
import com.example.starkfuturetest.domain.model.Diagnostics
import com.example.starkfuturetest.domain.model.MotorInfo
import com.example.starkfuturetest.domain.model.RideSettings
import com.example.starkfuturetest.domain.model.SessionInfo
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.usecase.GetTelemetrySnapshotUseCase
import com.example.starkfuturetest.presentation.dashboard.mapper.TelemetryUiMapper
import io.mockk.every
import io.mockk.verify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TelemetryDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetTelemetrySnapshotUseCase = mockk()
    private val uiMapper: TelemetryUiMapper = mockk()
    private val resources: ResourcesRepository = mockk {
        every { getString(any()) } answers { "error: ${firstArg<Int>()}" }
    }

    private val fakeSnapshot = TelemetrySnapshot(
        bike = BikeInfo("Stark VARG MX 1.2", "Alpha", "3.4.1", ""),
        timestamp = "2024-03-15T10:30:00Z",
        battery = BatteryInfo(73, 38, 34.7, "discharging"),
        motor = MotorInfo(52.4, 61.2),
        rideSettings = RideSettings("enduro", 80, 45, 60),
        session = SessionInfo(3742, 24.7, 94.1),
        diagnostics = Diagnostics(emptyList(), emptyList()),
    )

    private val fakeUiModel = TelemetryDashboardUiModel(
        bikeModel = "Stark VARG MX 1.2",
        variant = "Alpha",
        firmwareVersion = "3.4.1",
        formattedTimestamp = "2024-03-15 10:30",
        imageUrl = "",
        battery = BatteryUiModel(73, "73%", "Discharging", "38 km", "34.7°C", BatteryStatus.Healthy),
        motor = MotorUiModel("52.4 hp", "61.2°C"),
        rideSettings = RideSettingsUiModel("Enduro", "80 hp", "45%", "60%"),
        session = SessionUiModel("1h 02m", "24.7 km", "94.1 km/h", "23.8 km/h"),
        warnings = emptyList(),
        faultCodes = emptyList(),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = TelemetryDashboardViewModel(useCase, uiMapper, resources)

    @Test
    fun `initial state is Loading`() = runTest {
        every { useCase() } returns flowOf(AppResult.Success(fakeSnapshot))
        every { uiMapper.map(any()) } returns fakeUiModel

        assertEquals(TelemetryDashboardUiState.Loading, viewModel().uiState.value)
    }

    @Test
    fun `success result emits Content state`() = runTest {
        every { useCase() } returns flowOf(AppResult.Success(fakeSnapshot))
        every { uiMapper.map(fakeSnapshot) } returns fakeUiModel

        viewModel().uiState.test {
            skipItems(1) // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val content = awaitItem() as TelemetryDashboardUiState.Content
            assertEquals(fakeUiModel, content.data)
        }
    }

    @Test
    fun `blank bike model emits Empty state`() = runTest {
        val blankSnapshot = fakeSnapshot.copy(bike = fakeSnapshot.bike.copy(model = ""))
        every { useCase() } returns flowOf(AppResult.Success(blankSnapshot))

        viewModel().uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(TelemetryDashboardUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `parse error emits Error state`() = runTest {
        every { useCase() } returns flowOf(AppResult.Error(AppError.ParseError))

        viewModel().uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is TelemetryDashboardUiState.Error)
        }
    }

    @Test
    fun `asset read error emits Error state`() = runTest {
        every { useCase() } returns flowOf(AppResult.Error(AppError.AssetReadError))

        viewModel().uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is TelemetryDashboardUiState.Error)
        }
    }

    @Test
    fun `empty data error emits Empty state`() = runTest {
        every { useCase() } returns flowOf(AppResult.Error(AppError.EmptyData))

        viewModel().uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(TelemetryDashboardUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `unknown error emits Error state`() = runTest {
        every { useCase() } returns flowOf(AppResult.Error(AppError.Unknown()))

        viewModel().uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is TelemetryDashboardUiState.Error)
        }
    }

    @Test
    fun `subsequent flow emissions update content state`() = runTest {
        val secondSnapshot = fakeSnapshot.copy(battery = fakeSnapshot.battery.copy(stateOfChargePct = 55))
        val secondUiModel = fakeUiModel.copy(battery = fakeUiModel.battery.copy(stateOfChargePct = 55))
        every { useCase() } returns flow {
            emit(AppResult.Success(fakeSnapshot))
            emit(AppResult.Success(secondSnapshot))
        }
        every { uiMapper.map(fakeSnapshot) } returns fakeUiModel
        every { uiMapper.map(secondSnapshot) } returns secondUiModel

        viewModel().uiState.test {
            skipItems(1) // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(73, (awaitItem() as TelemetryDashboardUiState.Content).data.battery.stateOfChargePct)
            assertEquals(55, (awaitItem() as TelemetryDashboardUiState.Content).data.battery.stateOfChargePct)
        }
    }

    @Test
    fun `retry cancels current collection and restarts`() = runTest {
        every { useCase() } returnsMany listOf(
            flowOf(AppResult.Error(AppError.AssetReadError)),
            flowOf(AppResult.Success(fakeSnapshot)),
        )
        every { uiMapper.map(any()) } returns fakeUiModel

        val vm = viewModel()
        vm.uiState.test {
            skipItems(1) // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is TelemetryDashboardUiState.Error)

            vm.onAction(TelemetryDashboardAction.Retry)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(TelemetryDashboardUiState.Loading, awaitItem())
            assertTrue(awaitItem() is TelemetryDashboardUiState.Content)
        }
    }

    @Test
    fun `upstream is not collected while there are no subscribers`() = runTest {
        every { useCase() } returns flowOf(AppResult.Success(fakeSnapshot))
        every { uiMapper.map(any()) } returns fakeUiModel

        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // WhileSubscribed: without a collector the use case must never run.
        assertEquals(TelemetryDashboardUiState.Loading, vm.uiState.value)
        verify(exactly = 0) { useCase() }
    }

    @Test
    fun `default expanded section is Battery only`() = runTest {
        every { useCase() } returns flowOf(AppResult.Success(fakeSnapshot))
        every { uiMapper.map(any()) } returns fakeUiModel

        val expanded = viewModel().expandedSections.value
        assertTrue(TelemetrySectionId.Battery in expanded)
        assertFalse(TelemetrySectionId.Session in expanded)
        assertFalse(TelemetrySectionId.RideSettings in expanded)
    }

    @Test
    fun `toggle section expands and collapses`() = runTest {
        every { useCase() } returns flowOf(AppResult.Success(fakeSnapshot))
        every { uiMapper.map(any()) } returns fakeUiModel

        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(TelemetrySectionId.Battery in vm.expandedSections.value)
        vm.onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.Battery))
        assertFalse(TelemetrySectionId.Battery in vm.expandedSections.value)
        vm.onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.Battery))
        assertTrue(TelemetrySectionId.Battery in vm.expandedSections.value)
    }

    @Test
    fun `change theme updates themeMode`() = runTest {
        every { useCase() } returns flowOf(AppResult.Success(fakeSnapshot))
        every { uiMapper.map(any()) } returns fakeUiModel

        val vm = viewModel()
        assertEquals(ThemeMode.Dark, vm.themeMode.value)
        vm.onAction(TelemetryDashboardAction.ChangeTheme(ThemeMode.Light))
        assertEquals(ThemeMode.Light, vm.themeMode.value)
    }
}