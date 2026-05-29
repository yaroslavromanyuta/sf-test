package com.example.starkfuturetest.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starkfuturetest.R
import com.example.starkfuturetest.core.resources.ResourcesRepository
import com.example.starkfuturetest.core.result.AppError
import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.usecase.GetTelemetrySnapshotUseCase
import com.example.starkfuturetest.presentation.dashboard.mapper.TelemetryUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TelemetryDashboardViewModel @Inject constructor(
    private val getTelemetrySnapshotUseCase: GetTelemetrySnapshotUseCase,
    private val uiMapper: TelemetryUiMapper,
    private val resources: ResourcesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TelemetryDashboardUiState>(TelemetryDashboardUiState.Loading)
    val uiState: StateFlow<TelemetryDashboardUiState> = _uiState.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.Dark)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _expandedSections = MutableStateFlow<Set<TelemetrySectionId>>(
        setOf(TelemetrySectionId.Battery, TelemetrySectionId.Warnings),
    )
    val expandedSections: StateFlow<Set<TelemetrySectionId>> = _expandedSections.asStateFlow()

    init {
        loadTelemetry()
    }

    fun onAction(action: TelemetryDashboardAction) {
        when (action) {
            is TelemetryDashboardAction.Retry -> loadTelemetry()
            is TelemetryDashboardAction.ToggleSection -> toggleSection(action.sectionId)
            is TelemetryDashboardAction.ChangeTheme -> _themeMode.value = action.themeMode
        }
    }

    private fun loadTelemetry() {
        _uiState.value = TelemetryDashboardUiState.Loading
        viewModelScope.launch {
            when (val result = getTelemetrySnapshotUseCase()) {
                is AppResult.Success -> {
                    val snapshot = result.data
                    _uiState.value = if (snapshot.bike.model.isBlank()) {
                        TelemetryDashboardUiState.Empty
                    } else {
                        TelemetryDashboardUiState.Content(uiMapper.map(snapshot))
                    }
                }
                is AppResult.Error -> {
                    _uiState.value = TelemetryDashboardUiState.Error(errorMessage(result.error))
                }
            }
        }
    }

    private fun toggleSection(sectionId: TelemetrySectionId) {
        _expandedSections.update { current ->
            if (sectionId in current) current - sectionId else current + sectionId
        }
    }

    private fun errorMessage(error: AppError): String = when (error) {
        AppError.EmptyData -> resources.getString(R.string.error_message_empty_data)
        AppError.ParseError -> resources.getString(R.string.error_message_parse_error)
        AppError.AssetReadError -> resources.getString(R.string.error_message_asset_read)
        is AppError.Unknown -> resources.getString(R.string.error_message_unknown)
    }
}