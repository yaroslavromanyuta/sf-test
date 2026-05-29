package com.example.starkfuturetest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.starkfuturetest.R
import com.example.starkfuturetest.presentation.dashboard.WarningUiModel
import com.example.starkfuturetest.ui.theme.StarkColors
import com.example.starkfuturetest.ui.theme.StarkSpacing

@Composable
fun WarningBanner(
    warnings: List<WarningUiModel>,
    modifier: Modifier = Modifier,
) {
    if (warnings.isNotEmpty()) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(StarkSpacing.sm)) {
            warnings.forEach { SingleWarningBanner(it) }
        }
    }
}

@Composable
private fun SingleWarningBanner(warning: WarningUiModel) {
    val shape = RoundedCornerShape(StarkSpacing.sm)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = StarkColors.WarningAmber.copy(alpha = 0.1f), shape = shape)
            .border(width = 1.dp, color = StarkColors.WarningAmber.copy(alpha = 0.3f), shape = shape)
            .padding(StarkSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(StarkSpacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = stringResource(R.string.cd_warning_icon),
            tint = StarkColors.WarningAmber,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = warning.message,
            style = MaterialTheme.typography.titleMedium,
            color = StarkColors.WarningAmber,
        )
    }
}