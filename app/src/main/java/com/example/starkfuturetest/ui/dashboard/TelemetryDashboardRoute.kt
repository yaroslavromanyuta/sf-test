package com.example.starkfuturetest.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardAction
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiState
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardViewModel

@Composable
fun TelemetryDashboardRoute(
    viewModel: TelemetryDashboardViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val expandedSections by viewModel.expandedSections.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is TelemetryDashboardUiState.Loading -> TelemetryLoadingState(modifier = modifier)
        is TelemetryDashboardUiState.Empty -> TelemetryEmptyState(modifier = modifier)
        is TelemetryDashboardUiState.Error -> TelemetryErrorState(
            message = state.message,
            onRetry = { viewModel.onAction(TelemetryDashboardAction.Retry) },
            modifier = modifier,
        )
        is TelemetryDashboardUiState.Content -> TelemetryDashboardScreen(
            data = state.data,
            expandedSections = expandedSections,
            currentTheme = themeMode,
            onAction = viewModel::onAction,
            modifier = modifier,
        )
    }
}