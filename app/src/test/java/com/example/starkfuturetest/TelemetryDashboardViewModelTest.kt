package com.example.starkfuturetest

import app.cash.turbine.test
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
import com.example.starkfuturetest.presentation.dashboard.BatteryUiModel
import com.example.starkfuturetest.presentation.dashboard.MotorUiModel
import com.example.starkfuturetest.presentation.dashboard.RideSettingsUiModel
import com.example.starkfuturetest.presentation.dashboard.SessionUiModel
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardAction
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiModel
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiState
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardViewModel
import com.example.starkfuturetest.presentation.dashboard.TelemetrySectionId
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
import com.example.starkfuturetest.presentation.dashboard.mapper.TelemetryUiMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        coEvery { useCase() } coAnswers { kotlinx.coroutines.delay(1000); AppResult.Success(fakeSnapshot) }
        coEvery { uiMapper.map(any()) } returns fakeUiModel

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        assertEquals(TelemetryDashboardUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `success result emits Content state`() = runTest {
        coEvery { useCase() } returns AppResult.Success(fakeSnapshot)
        coEvery { uiMapper.map(fakeSnapshot) } returns fakeUiModel

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        viewModel.uiState.test {
            skipItems(1) // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val content = awaitItem()
            assertTrue(content is TelemetryDashboardUiState.Content)
            assertEquals(fakeUiModel, (content as TelemetryDashboardUiState.Content).data)
        }
    }

    @Test
    fun `blank model emits Empty state`() = runTest {
        val blankSnapshot = fakeSnapshot.copy(bike = fakeSnapshot.bike.copy(model = ""))
        coEvery { useCase() } returns AppResult.Success(blankSnapshot)

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        viewModel.uiState.test {
            skipItems(1) // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(TelemetryDashboardUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `parse error emits Error state with correct message`() = runTest {
        coEvery { useCase() } returns AppResult.Error(AppError.ParseError)

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        viewModel.uiState.test {
            skipItems(1) // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val error = awaitItem()
            assertTrue(error is TelemetryDashboardUiState.Error)
            assertEquals("Failed to parse telemetry snapshot", (error as TelemetryDashboardUiState.Error).message)
        }
    }

    @Test
    fun `toggle section expands and collapses`() = runTest {
        coEvery { useCase() } returns AppResult.Success(fakeSnapshot)
        coEvery { uiMapper.map(any()) } returns fakeUiModel
        testDispatcher.scheduler.advanceUntilIdle()

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(TelemetrySectionId.Battery in viewModel.expandedSections.value)
        viewModel.onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.Battery))
        assertFalse(TelemetrySectionId.Battery in viewModel.expandedSections.value)
        viewModel.onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.Battery))
        assertTrue(TelemetrySectionId.Battery in viewModel.expandedSections.value)
    }

    @Test
    fun `default expanded sections are Battery and Warnings`() = runTest {
        coEvery { useCase() } returns AppResult.Success(fakeSnapshot)
        coEvery { uiMapper.map(any()) } returns fakeUiModel

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        val expanded = viewModel.expandedSections.value
        assertTrue(TelemetrySectionId.Battery in expanded)
        assertTrue(TelemetrySectionId.Warnings in expanded)
        assertFalse(TelemetrySectionId.Session in expanded)
    }

    @Test
    fun `change theme updates themeMode`() = runTest {
        coEvery { useCase() } returns AppResult.Success(fakeSnapshot)
        coEvery { uiMapper.map(any()) } returns fakeUiModel

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        assertEquals(ThemeMode.Dark, viewModel.themeMode.value)
        viewModel.onAction(TelemetryDashboardAction.ChangeTheme(ThemeMode.Light))
        assertEquals(ThemeMode.Light, viewModel.themeMode.value)
    }

    @Test
    fun `retry reloads telemetry`() = runTest {
        coEvery { useCase() } returnsMany listOf(
            AppResult.Error(AppError.AssetReadError),
            AppResult.Success(fakeSnapshot),
        )
        coEvery { uiMapper.map(any()) } returns fakeUiModel

        val viewModel = TelemetryDashboardViewModel(useCase, uiMapper)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is TelemetryDashboardUiState.Error)

        viewModel.onAction(TelemetryDashboardAction.Retry)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is TelemetryDashboardUiState.Content)
    }
}