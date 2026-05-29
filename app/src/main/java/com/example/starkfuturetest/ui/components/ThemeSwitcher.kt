package com.example.starkfuturetest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.starkfuturetest.presentation.dashboard.ThemeMode
import com.example.starkfuturetest.ui.theme.StarkSpacing

@Composable
fun ThemeSwitcher(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pillShape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = pillShape)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = pillShape)
            .padding(StarkSpacing.xs),
    ) {
        ThemeMode.entries.forEach { mode ->
            val isSelected = mode == currentTheme
            Text(
                text = mode.name,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .then(
                        if (isSelected) Modifier.background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(50),
                        ) else Modifier,
                    )
                    .clickable { onThemeChange(mode) }
                    .padding(horizontal = StarkSpacing.md, vertical = StarkSpacing.sm)
                    .semantics { contentDescription = "${mode.name} theme" },
            )
        }
    }
}