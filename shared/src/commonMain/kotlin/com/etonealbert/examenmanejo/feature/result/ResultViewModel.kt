package com.etonealbert.examenmanejo.feature.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.usecase.GetExamResultUseCase
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

class ResultViewModel(
    private val examSessionId: Long,
    private val getExamResult: GetExamResultUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResultUiState(examSessionId = examSessionId))
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ResultUiEffect>()
    val effects: SharedFlow<ResultUiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch(dispatcher) {
            val result = getExamResult(examSessionId)
            _uiState.update { it.copy(isLoading = false, result = result) }
        }
    }

    fun onEvent(event: ResultUiEvent) {
        val route = when (event) {
            ResultUiEvent.ReviewClicked -> AppRoute.Review(examSessionId)
            ResultUiEvent.HistoryClicked -> AppRoute.History
            ResultUiEvent.HomeClicked -> AppRoute.Home
        }
        viewModelScope.launch(dispatcher) { _effects.emit(ResultUiEffect.Navigate(route)) }
    }
}
