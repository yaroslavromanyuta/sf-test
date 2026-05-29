package com.example.starkfuturetest.presentation.dashboard

sealed interface TelemetryDashboardAction {
    data object Retry : TelemetryDashboardAction
    data class ToggleSection(val sectionId: TelemetrySectionId) : TelemetryDashboardAction
    data class ChangeTheme(val themeMode: ThemeMode) : TelemetryDashboardAction
}