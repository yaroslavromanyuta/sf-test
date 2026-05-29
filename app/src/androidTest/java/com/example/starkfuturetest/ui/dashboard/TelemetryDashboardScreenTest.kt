package com.example.starkfuturetest.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.starkfuturetest.domain.model.BatteryStatus
import com.example.starkfuturetest.presentation.dashboard.BatteryUiModel
import com.example.starkfuturetest.presentation.dashboard.MotorUiModel
import com.example.starkfuturetest.presentation.dashboard.RideSettingsUiModel
import com.example.starkfuturetest.presentation.dashboard.SessionUiModel
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardAction
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiModel
import com.example.starkfuturetest.presentation.dashboard.TelemetrySectionId
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
import com.example.starkfuturetest.presentation.dashboard.WarningUiModel
import com.example.starkfuturetest.ui.theme.StarkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TelemetryDashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeUiModel = TelemetryDashboardUiModel(
        bikeModel = "Stark VARG MX 1.2",
        variant = "Alpha",
        firmwareVersion = "3.4.1",
        formattedTimestamp = "2025-05-19 10:32",
        imageUrl = "",
        battery = BatteryUiModel(
            stateOfChargePct = 73,
            displayCharge = "73%",
            chargingState = "Discharging",
            estimatedRange = "38 km",
            temperatureC = "34.7°C",
            batteryStatus = BatteryStatus.Healthy,
        ),
        motor = MotorUiModel(power = "52.4 hp", temperatureC = "61.2°C"),
        rideSettings = RideSettingsUiModel(
            powerMap = "Enduro",
            maxPower = "80.0 hp",
            engineBraking = "45%",
            regen = "60%",
        ),
        session = SessionUiModel(
            duration = "1h 02m",
            distance = "24.7 km",
            maxSpeed = "94.1 km/h",
            averageSpeed = "23.8 km/h",
        ),
        warnings = listOf(
            WarningUiModel(
                code = "W_MOT_TEMP_HIGH",
                message = "Motor temperature elevated",
                severity = "Warning",
            ),
        ),
        faultCodes = emptyList(),
    )

    private fun setContent(
        uiModel: TelemetryDashboardUiModel = fakeUiModel,
        expandedSections: Set<TelemetrySectionId> = setOf(TelemetrySectionId.Battery),
        onAction: (TelemetryDashboardAction) -> Unit = {},
    ) {
        composeTestRule.setContent {
            StarkTheme {
                TelemetryDashboardScreen(
                    data = uiModel,
                    expandedSections = expandedSections,
                    currentTheme = ThemeMode.Dark,
                    onAction = onAction,
                )
            }
        }
    }

    @Test
    fun bikeModelIsDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("Stark VARG MX 1.2").assertIsDisplayed()
    }

    @Test
    fun timestampIsDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("2025-05-19 10:32").assertIsDisplayed()
    }

    @Test
    fun warningMessageIsDisplayedWhenWarningsPresent() {
        setContent()
        composeTestRule.onNodeWithText("Motor temperature elevated").assertIsDisplayed()
    }

    @Test
    fun healthyBannerShownWhenNoWarnings() {
        setContent(uiModel = fakeUiModel.copy(warnings = emptyList()))
        composeTestRule.onNodeWithText("Healthy · No active warnings").assertIsDisplayed()
    }

    @Test
    fun batteryPercentageDisplayedInCompactView() {
        setContent()
        composeTestRule.onNodeWithText("73%").assertIsDisplayed()
    }

    @Test
    fun batteryStatusChipIsDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("HEALTHY").assertIsDisplayed()
    }

    @Test
    fun expandedBatteryShowsEstimatedRange() {
        setContent(expandedSections = setOf(TelemetrySectionId.Battery))
        composeTestRule.onNodeWithText("38 km").assertIsDisplayed()
    }

    @Test
    fun expandedBatteryShowsTemperature() {
        setContent(expandedSections = setOf(TelemetrySectionId.Battery))
        composeTestRule.onNodeWithText("34.7°C").assertIsDisplayed()
    }

    @Test
    fun collapsedBatteryHidesExpandedContent() {
        setContent(expandedSections = emptySet())
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("38 km").assertDoesNotExist()
        composeTestRule.onNodeWithText("34.7°C").assertDoesNotExist()
    }

    @Test
    fun batteryToggleActionFired() {
        var lastAction: TelemetryDashboardAction? = null
        setContent(
            expandedSections = setOf(TelemetrySectionId.Battery),
            onAction = { lastAction = it },
        )
        composeTestRule.onNodeWithContentDescription("Collapse Battery").performClick()
        assert(lastAction == TelemetryDashboardAction.ToggleSection(TelemetrySectionId.Battery))
    }

    @Test
    fun motorPowerIsDisplayed() {
        setContent()
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("52.4 hp"))
        composeTestRule.onNodeWithText("52.4 hp").assertIsDisplayed()
    }

    @Test
    fun motorTemperatureIsDisplayed() {
        setContent()
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("61.2°C"))
        composeTestRule.onNodeWithText("61.2°C").assertIsDisplayed()
    }

    @Test
    fun faultCodesSectionHiddenWhenEmpty() {
        setContent(uiModel = fakeUiModel.copy(faultCodes = emptyList()))
        composeTestRule.onNodeWithText("Fault Codes").assertDoesNotExist()
    }

    @Test
    fun faultCodesSectionShownWhenNonEmpty() {
        setContent(uiModel = fakeUiModel.copy(faultCodes = listOf("E_SENS_THROTTLE_OOR")))
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("Fault Codes"))
        composeTestRule.onNodeWithText("Fault Codes").assertIsDisplayed()
    }

    @Test
    fun faultCodeValueIsDisplayed() {
        setContent(
            uiModel = fakeUiModel.copy(faultCodes = listOf("E_SENS_THROTTLE_OOR")),
            expandedSections = setOf(TelemetrySectionId.FaultCodes),
        )
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("E_SENS_THROTTLE_OOR"))
        composeTestRule.onNodeWithText("E_SENS_THROTTLE_OOR").assertIsDisplayed()
    }

    @Test
    fun maxSpeedLabelIsDisplayed() {
        setContent(expandedSections = setOf(TelemetrySectionId.Session))
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("MAX SPEED"))
        composeTestRule.onNodeWithText("MAX SPEED").assertIsDisplayed()
    }

    @Test
    fun maxSpeedValueIsDisplayed() {
        setContent(expandedSections = setOf(TelemetrySectionId.Session))
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("94.1 km/h"))
        composeTestRule.onNodeWithText("94.1 km/h").assertIsDisplayed()
    }

    @Test
    fun rideModeIsDisplayed() {
        setContent(expandedSections = setOf(TelemetrySectionId.RideSettings))
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("Enduro"))
        composeTestRule.onNodeWithText("Enduro").assertIsDisplayed()
    }
}