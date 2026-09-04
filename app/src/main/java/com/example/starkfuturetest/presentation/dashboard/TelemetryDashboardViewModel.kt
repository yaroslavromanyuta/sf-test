package com.example.starkfuturetest.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starkfuturetest.R
import com.example.starkfuturetest.core.resources.ResourcesRepository
import com.example.starkfuturetest.core.result.AppError
import com.example.starkfuturetest.core.result.AppResult
import com.example.starkfuturetest.domain.model.TelemetrySnapshot
import com.example.starkfuturetest.domain.usecase.GetTelemetrySnapshotUseCase
import com.example.starkfuturetest.presentation.dashboard.mapper.TelemetryUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@HiltViewModel
class TelemetryDashboardViewModel @Inject constructor(
    private val getTelemetrySnapshotUseCase: GetTelemetrySnapshotUseCase,
    private val uiMapper: TelemetryUiMapper,
    private val resources: ResourcesRepository,
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TelemetryDashboardUiState> = retryTrigger
        .flatMapLatest {
            getTelemetrySnapshotUseCase()
                .map(::toUiState)
                .onStart { emit(TelemetryDashboardUiState.Loading) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
            initialValue = TelemetryDashboardUiState.Loading,
        )

    private val _themeMode = MutableStateFlow(ThemeMode.Dark)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _expandedSections = MutableStateFlow<PersistentSet<TelemetrySectionId>>(
        persistentSetOf(TelemetrySectionId.Battery),
    )
    val expandedSections: StateFlow<PersistentSet<TelemetrySectionId>> = _expandedSections.asStateFlow()

    fun onAction(action: TelemetryDashboardAction) {
        when (action) {
            is TelemetryDashboardAction.Retry -> retryTrigger.update { it + 1 }
            is TelemetryDashboardAction.ToggleSection -> toggleSection(action.sectionId)
            is TelemetryDashboardAction.ChangeTheme -> _themeMode.value = action.themeMode
        }
    }

    private fun toUiState(result: AppResult<TelemetrySnapshot>): TelemetryDashboardUiState = when (result) {
        is AppResult.Success -> {
            val snapshot = result.data
            if (snapshot.bike.model.isBlank()) {
                TelemetryDashboardUiState.Empty
            } else {
                TelemetryDashboardUiState.Content(uiMapper.map(snapshot))
            }
        }
        is AppResult.Error -> when (result.error) {
            AppError.EmptyData -> TelemetryDashboardUiState.Empty
            else -> TelemetryDashboardUiState.Error(errorMessage(result.error))
        }
    }

    private fun toggleSection(sectionId: TelemetrySectionId) {
        _expandedSections.update { current ->
            if (sectionId in current) current.remove(sectionId) else current.add(sectionId)
        }
    }

    private fun errorMessage(error: AppError): String = when (error) {
        AppError.EmptyData -> resources.getString(R.string.error_message_empty_data)
        AppError.ParseError -> resources.getString(R.string.error_message_parse_error)
        AppError.AssetReadError -> resources.getString(R.string.error_message_asset_read)
        is AppError.Unknown -> resources.getString(R.string.error_message_unknown)
    }
}
