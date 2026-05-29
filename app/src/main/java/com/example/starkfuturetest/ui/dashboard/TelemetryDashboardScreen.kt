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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.starkfuturetest.R
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardAction
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiModel
import com.example.starkfuturetest.presentation.dashboard.TelemetrySectionId
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                scrollBehavior = scrollBehavior,
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
                                    Row(
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