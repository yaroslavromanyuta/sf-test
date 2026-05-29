package com.example.starkfuturetest.presentation.dashboard

sealed interface TelemetryDashboardUiState {
    data object Loading : TelemetryDashboardUiState
    data class Content(val data: TelemetryDashboardUiModel) : TelemetryDashboardUiState
    data object Empty : TelemetryDashboardUiState
    data class Error(val message: String) : TelemetryDashboardUiState
}