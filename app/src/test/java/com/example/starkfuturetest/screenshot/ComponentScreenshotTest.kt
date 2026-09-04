package com.example.starkfuturetest.screenshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.example.starkfuturetest.domain.model.BatteryStatus
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
import com.example.starkfuturetest.presentation.dashboard.WarningUiModel
import com.example.starkfuturetest.ui.components.BatteryProgressIndicator
import com.example.starkfuturetest.ui.components.WarningBanner
import com.example.starkfuturetest.ui.theme.StarkTheme
import org.junit.Rule
import org.junit.Test
import kotlinx.collections.immutable.persistentListOf

class ComponentScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        theme = "android:Theme.Material.Light.NoActionBar",
    )

    // ── WarningBanner ──────────────────────────────────────────────────────────

    @Test
    fun warning_banner_single_warning() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                WarningBanner(
                    warnings = persistentListOf(
                        WarningUiModel("W_MOT_TEMP_HIGH", "Motor temperature elevated", "Warning"),
                    ),
                )
            }
        }
    }

    @Test
    fun warning_banner_multiple_warnings() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                WarningBanner(
                    warnings = persistentListOf(
                        WarningUiModel("W_MOT_TEMP_HIGH", "Motor temperature elevated", "Warning"),
                        WarningUiModel("W_BATT_CRITICAL", "Battery critically low", "Critical"),
                    ),
                )
            }
        }
    }

    @Test
    fun warning_banner_empty() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                WarningBanner(warnings = persistentListOf())
            }
        }
    }

    // ── BatteryProgressIndicator ───────────────────────────────────────────────

    @Test
    fun battery_indicator_healthy() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                BatteryProgressIndicator(stateOfChargePct = 73, batteryStatus = BatteryStatus.Healthy)
            }
        }
    }

    @Test
    fun battery_indicator_medium() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                BatteryProgressIndicator(stateOfChargePct = 30, batteryStatus = BatteryStatus.Medium)
            }
        }
    }

    @Test
    fun battery_indicator_critical() {
        paparazzi.snapshot {
            StarkTheme(ThemeMode.Dark) {
                BatteryProgressIndicator(stateOfChargePct = 8, batteryStatus = BatteryStatus.Critical)
            }
        }
    }
}