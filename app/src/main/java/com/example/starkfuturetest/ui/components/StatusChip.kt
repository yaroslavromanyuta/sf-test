package com.example.starkfuturetest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.starkfuturetest.domain.model.BatteryStatus
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
import com.example.starkfuturetest.ui.theme.StarkSpacing
import com.example.starkfuturetest.ui.theme.StarkTheme

@Composable
fun StatusChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StarkSpacing.sm)
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
            .background(color = color.copy(alpha = 0.12f), shape = shape)
            .border(width = 1.dp, color = color.copy(alpha = 0.3f), shape = shape)
            .padding(horizontal = StarkSpacing.sm, vertical = StarkSpacing.xs),
    )
}

@Preview(name = "Chip – Healthy")
@Composable
private fun StatusChipHealthyPreview() {
    StarkTheme(ThemeMode.Dark) {
        StatusChip(label = BatteryStatus.Healthy.name, color = batteryStatusColor(BatteryStatus.Healthy))
    }
}

@Preview(name = "Chip – Medium")
@Composable
private fun StatusChipMediumPreview() {
    StarkTheme(ThemeMode.Dark) {
        StatusChip(label = BatteryStatus.Medium.name, color = batteryStatusColor(BatteryStatus.Medium))
    }
}

@Preview(name = "Chip – Critical")
@Composable
private fun StatusChipCriticalPreview() {
    StarkTheme(ThemeMode.Dark) {
        StatusChip(label = BatteryStatus.Critical.name, color = batteryStatusColor(BatteryStatus.Critical))
    }
}