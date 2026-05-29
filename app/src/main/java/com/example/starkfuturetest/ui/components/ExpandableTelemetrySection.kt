package com.example.starkfuturetest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.starkfuturetest.R
import com.example.starkfuturetest.ui.theme.StarkSpacing

@Composable
fun ExpandableTelemetrySection(
    title: String,
    icon: @Composable () -> Unit,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    expandedContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    compactContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val toggleDescription = if (isExpanded) {
        stringResource(R.string.cd_section_collapse, title)
    } else {
        stringResource(R.string.cd_section_expand, title)
    }

    TelemetryCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .semantics { contentDescription = toggleDescription },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(StarkSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon()
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp
                              else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (compactContent != null) {
            Spacer(modifier = Modifier.height(StarkSpacing.md))
            compactContent()
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                if (compactContent != null) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = StarkSpacing.md),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                } else {
                    Spacer(modifier = Modifier.height(StarkSpacing.md))
                }
                expandedContent()
            }
        }
    }
}