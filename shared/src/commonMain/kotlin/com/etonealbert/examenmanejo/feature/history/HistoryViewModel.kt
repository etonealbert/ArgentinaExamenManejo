package com.etonealbert.examenmanejo.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.usecase.GetExamHistoryUseCase
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

class HistoryViewModel(
    private val getExamHistory: GetExamHistoryUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<HistoryUiEffect>()
    val effects: SharedFlow<HistoryUiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch(dispatcher) {
            val results = getExamHistory()
            _uiState.update { it.copy(isLoading = false, results = results) }
        }
    }

    fun onEvent(event: HistoryUiEvent) {
        val route = when (event) {
            is HistoryUiEvent.ResultClicked -> AppRoute.Result(event.examSessionId)
            HistoryUiEvent.HomeClicked -> AppRoute.Home
        }
        viewModelScope.launch(dispatcher) { _effects.emit(HistoryUiEffect.Navigate(route)) }
    }
}
