package com.example.starkfuturetest.presentation.dashboard.mapper

import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardUiModel

interface TelemetryUiMapper {
    fun map(snapshot: TelemetrySnapshot): TelemetryDashboardUiModel
}