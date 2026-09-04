package com.example.starkfuturetest.screenshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.example.starkfuturetest.presentation.dashboard.TelemetrySectionId
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
import com.example.starkfuturetest.ui.dashboard.TelemetryDashboardScreen
import com.example.starkfuturetest.ui.dashboard.TelemetryEmptyState
import com.example.starkfuturetest.ui.dashboard.TelemetryErrorState
import com.example.starkfuturetest.ui.dashboard.TelemetryLoadingState
import com.example.starkfuturetest.ui.theme.StarkTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.persistentListOf

class DashboardScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        theme = "android:Theme.Material.Light.NoActionBar",
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Loading state ──────────────────────────────────────────────────────────

    @Test
    fun loading_dark() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) { TelemetryLoadingState() }
        }
    }

    @Test
    fun loading_light() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Light) { TelemetryLoadingState() }
        }
    }

    // ── Empty state ────────────────────────────────────────────────────────────

    @Test
    fun empty_dark() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) { TelemetryEmptyState() }
        }
    }

    @Test
    fun empty_light() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Light) { TelemetryEmptyState() }
        }
    }

    // ── Error state ────────────────────────────────────────────────────────────

    @Test
    fun error_dark() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                TelemetryErrorState(message = "Could not read telemetry data", onRetry = {})
            }
        }
    }

    @Test
    fun error_light() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Light) {
                TelemetryErrorState(message = "Could not read telemetry data", onRetry = {})
            }
        }
    }

    // ── Content – dark ─────────────────────────────────────────────────────────

    @Test
    fun content_dark_battery_expanded() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                TelemetryDashboardScreen(
                    data = fakeDashboardUiModel,
                    expandedSections = persistentSetOf(TelemetrySectionId.Battery),
                    currentTheme = ThemeMode.Dark,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun content_dark_battery_collapsed() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                TelemetryDashboardScreen(
                    data = fakeDashboardUiModel,
                    expandedSections = persistentSetOf(),
                    currentTheme = ThemeMode.Dark,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun content_dark_no_warnings_no_faults() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                TelemetryDashboardScreen(
                    data = fakeDashboardUiModel.copy(warnings = persistentListOf(), faultCodes = persistentListOf()),
                    expandedSections = persistentSetOf(TelemetrySectionId.Battery),
                    currentTheme = ThemeMode.Dark,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun content_dark_critical_battery_multiple_warnings() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                TelemetryDashboardScreen(
                    data = fakeDashboardUiModelCritical,
                    expandedSections = persistentSetOf(TelemetrySectionId.Battery),
                    currentTheme = ThemeMode.Dark,
                    onAction = {},
                )
            }
        }
    }

    // ── Content – light ────────────────────────────────────────────────────────

    @Test
    fun content_light_battery_expanded() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Light) {
                TelemetryDashboardScreen(
                    data = fakeDashboardUiModel,
                    expandedSections = persistentSetOf(TelemetrySectionId.Battery),
                    currentTheme = ThemeMode.Light,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun content_light_no_warnings() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Light) {
                TelemetryDashboardScreen(
                    data = fakeDashboardUiModel.copy(warnings = persistentListOf(), faultCodes = persistentListOf()),
                    expandedSections = persistentSetOf(TelemetrySectionId.Battery),
                    currentTheme = ThemeMode.Light,
                    onAction = {},
                )
            }
        }
    }
}