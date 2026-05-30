package com.example.starkfuturetest.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.starkfuturetest.R
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
import com.example.starkfuturetest.ui.components.BatteryProgressIndicator
import com.example.starkfuturetest.ui.components.BikeHeaderCard
import com.example.starkfuturetest.ui.components.ExpandableTelemetrySection
import com.example.starkfuturetest.ui.components.StaticTelemetrySection
import com.example.starkfuturetest.ui.components.MetricTile
import com.example.starkfuturetest.ui.components.StatusChip
import com.example.starkfuturetest.ui.components.ThemeSwitcher
import com.example.starkfuturetest.ui.components.WarningBanner
import com.example.starkfuturetest.ui.components.batteryStatusColor
import com.example.starkfuturetest.ui.theme.StarkSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemetryDashboardScreen(
    data: TelemetryDashboardUiModel,
    expandedSections: Set<TelemetrySectionId>,
    currentTheme: ThemeMode,
    onAction: (TelemetryDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                actions = {
                    ThemeSwitcher(
                        currentTheme = currentTheme,
                        onThemeChange = { onAction(TelemetryDashboardAction.ChangeTheme(it)) },
                        modifier = Modifier.padding(end = StarkSpacing.sm),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = StarkSpacing.gutter,
                end = StarkSpacing.gutter,
                top = innerPadding.calculateTopPadding() + StarkSpacing.md,
                bottom = innerPadding.calculateBottomPadding() + StarkSpacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(StarkSpacing.md),
        ) {
            item {
                BikeHeaderCard(
                    model = data.bikeModel,
                    variant = data.variant,
                    firmwareVersion = data.firmwareVersion,
                    formattedTimestamp = data.formattedTimestamp,
                    imageUrl = data.imageUrl,
                )
            }

            item {
                WarningBanner(warnings = data.warnings)
            }

            item {
                ExpandableTelemetrySection(
                    title = stringResource(R.string.section_battery),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    isExpanded = TelemetrySectionId.Battery in expandedSections,
                    onToggle = { onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.Battery)) },
                    compactContent = {
                        BatteryProgressIndicator(
                            stateOfChargePct = data.battery.stateOfChargePct,
                            batteryStatus = data.battery.batteryStatus,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(StarkSpacing.sm))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = data.battery.displayCharge,
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            StatusChip(
                                label = data.battery.batteryStatus.name,
                                color = batteryStatusColor(data.battery.batteryStatus),
                            )
                        }
                    },
                    expandedContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                        ) {
                            MetricTile(
                                label = stringResource(R.string.metric_estimated_range),
                                value = data.battery.estimatedRange,
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = stringResource(R.string.metric_temperature),
                                value = data.battery.temperatureC,
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = stringResource(R.string.metric_state),
                                value = data.battery.chargingState,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    },
                )
            }

            item {
                StaticTelemetrySection(
                    title = stringResource(R.string.section_performance),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                        ) {
                            MetricTile(
                                label = stringResource(R.string.metric_power),
                                value = data.motor.power,
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = stringResource(R.string.metric_motor_temp),
                                value = data.motor.temperatureC,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    },
                )
            }

            item {
                ExpandableTelemetrySection(
                    title = stringResource(R.string.section_ride_settings),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    isExpanded = TelemetrySectionId.RideSettings in expandedSections,
                    onToggle = { onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.RideSettings)) },
                    expandedContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                        ) {
                            MetricTile(
                                label = stringResource(R.string.metric_power_map),
                                value = data.rideSettings.powerMap,
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = stringResource(R.string.metric_max_power),
                                value = data.rideSettings.maxPower,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(modifier = Modifier.height(StarkSpacing.sm))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                        ) {
                            MetricTile(
                                label = stringResource(R.string.metric_engine_braking),
                                value = data.rideSettings.engineBraking,
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = stringResource(R.string.metric_regen),
                                value = data.rideSettings.regen,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    },
                )
            }

            item {
                ExpandableTelemetrySection(
                    title = stringResource(R.string.section_session),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    isExpanded = TelemetrySectionId.Session in expandedSections,
                    onToggle = { onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.Session)) },
                    expandedContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                        ) {
                            MetricTile(
                                label = stringResource(R.string.metric_duration),
                                value = data.session.duration,
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = stringResource(R.string.metric_distance),
                                value = data.session.distance,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(modifier = Modifier.height(StarkSpacing.sm))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                        ) {
                            MetricTile(
                                label = stringResource(R.string.metric_max_speed),
                                value = data.session.maxSpeed,
                                modifier = Modifier.weight(1f),
                            )
                            MetricTile(
                                label = stringResource(R.string.metric_avg_speed),
                                value = data.session.averageSpeed,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    },
                )
            }

            if (data.faultCodes.isNotEmpty()) {
                item {
                    ExpandableTelemetrySection(
                        title = stringResource(R.string.section_fault_codes),
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                        },
                        isExpanded = TelemetrySectionId.FaultCodes in expandedSections,
                        onToggle = { onAction(TelemetryDashboardAction.ToggleSection(TelemetrySectionId.FaultCodes)) },
                        expandedContent = {
                            Column(verticalArrangement = Arrangement.spacedBy(StarkSpacing.sm)) {
                                data.faultCodes.forEach { code ->
                                    val codeDesc = stringResource(R.string.cd_fault_code, code)
                                    Row(
                                        modifier = Modifier.semantics { contentDescription = codeDesc },
                                        horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = code,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

private val previewUiModel = TelemetryDashboardUiModel(
    bikeModel = "Stark VARG MX 1.2",
    variant = "Alpha",
    firmwareVersion = "3.4.1",
    formattedTimestamp = "2025-05-19 10:32",
    imageUrl = "",
    battery = BatteryUiModel(73, "73%", "Discharging", "38 km", "34.7°C", BatteryStatus.Healthy),
    motor = MotorUiModel("52.4 hp", "61.2°C"),
    rideSettings = RideSettingsUiModel("Enduro", "80.0 hp", "45%", "60%"),
    session = SessionUiModel("1h 02m", "24.7 km", "94.1 km/h", "23.8 km/h"),
    warnings = listOf(WarningUiModel("W_MOT_TEMP_HIGH", "Motor temperature elevated", "Warning")),
    faultCodes = listOf("E_SENS_THROTTLE_OOR"),
)

@Preview(name = "Dashboard – Dark with warnings")
@Composable
private fun TelemetryDashboardScreenPreviewDark() {
    StarkTheme(ThemeMode.Dark) {
        TelemetryDashboardScreen(
            data = previewUiModel,
            expandedSections = setOf(TelemetrySectionId.Battery),
            currentTheme = ThemeMode.Dark,
            onAction = {},
        )
    }
}

@Preview(name = "Dashboard – Light no warnings")
@Composable
private fun TelemetryDashboardScreenPreviewLight() {
    StarkTheme(ThemeMode.Light) {
        TelemetryDashboardScreen(
            data = previewUiModel.copy(warnings = emptyList(), faultCodes = emptyList()),
            expandedSections = setOf(TelemetrySectionId.Battery),
            currentTheme = ThemeMode.Light,
            onAction = {},
        )
    }
}