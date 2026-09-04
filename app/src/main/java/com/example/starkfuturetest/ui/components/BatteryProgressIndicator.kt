package com.example.starkfuturetest.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.starkfuturetest.R
import com.example.starkfuturetest.domain.model.BatteryStatus
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
import com.example.starkfuturetest.ui.theme.StarkColors
import com.example.starkfuturetest.ui.theme.StarkTheme

@Composable
fun BatteryProgressIndicator(
    stateOfChargePct: Int,
    batteryStatus: BatteryStatus,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.cd_battery_indicator, stateOfChargePct, batteryStatus.name)
    LinearProgressIndicator(
        progress = { stateOfChargePct / 100f },
        modifier = modifier.fillMaxWidth().semantics { contentDescription = description },
        color = batteryStatusColor(batteryStatus),
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun batteryStatusColor(status: BatteryStatus): Color {
    val isDark = MaterialTheme.colorScheme.background == StarkColors.BackgroundDark
    return when (status) {
        BatteryStatus.Healthy -> if (isDark) StarkColors.SuccessGreen else StarkColors.SuccessGreenLight
        BatteryStatus.Medium -> if (isDark) StarkColors.WarningAmber else StarkColors.WarningAmberLight
        BatteryStatus.Critical -> if (isDark) StarkColors.StarkRed else StarkColors.ErrorRedLight
        BatteryStatus.Unknown -> StarkColors.OutlineDark
    }
}

@Preview(name = "Battery – Healthy 73%")
@Composable
private fun BatteryProgressHealthyPreview() {
    StarkTheme(ThemeMode.Dark) {
        BatteryProgressIndicator(stateOfChargePct = 73, batteryStatus = BatteryStatus.Healthy)
    }
}

@Preview(name = "Battery – Medium 30%")
@Composable
private fun BatteryProgressMediumPreview() {
    StarkTheme(ThemeMode.Dark) {
        BatteryProgressIndicator(stateOfChargePct = 30, batteryStatus = BatteryStatus.Medium)
    }
}

@Preview(name = "Battery – Critical 8%")
@Composable
private fun BatteryProgressCriticalPreview() {
    StarkTheme(ThemeMode.Dark) {
        BatteryProgressIndicator(stateOfChargePct = 8, batteryStatus = BatteryStatus.Critical)
    }
}