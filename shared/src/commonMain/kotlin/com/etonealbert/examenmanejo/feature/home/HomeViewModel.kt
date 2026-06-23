package com.etonealbert.examenmanejo.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.usecase.GetLicenseClassesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getLicenseClasses: GetLicenseClassesUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<HomeUiEffect>()
    val effects: SharedFlow<HomeUiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch(dispatcher) {
            getLicenseClasses().collect { classes ->
                _uiState.update { it.copy(isLoading = false, licenseClasses = classes, errorMessage = null) }
            }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.StartExamClicked -> emitRoute(AppRoute.Exam(event.licenseClassId))
            is HomeUiEvent.StudyClicked -> emitRoute(AppRoute.Study(event.licenseClassId))
            HomeUiEvent.HistoryClicked -> emitRoute(AppRoute.History)
            HomeUiEvent.SettingsClicked -> emitRoute(AppRoute.Settings)
        }
    }

    private fun emitRoute(route: AppRoute) {
        viewModelScope.launch(dispatcher) { _effects.emit(HomeUiEffect.Navigate(route)) }
    }
}
