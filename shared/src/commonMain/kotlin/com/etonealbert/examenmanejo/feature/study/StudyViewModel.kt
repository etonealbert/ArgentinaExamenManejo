package com.etonealbert.examenmanejo.feature.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.model.QuestionStatus
import com.etonealbert.examenmanejo.domain.usecase.GetQuestionsByLicenseClassUseCase
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

class StudyViewModel(
    licenseClassId: String,
    private val getQuestionsByLicenseClass: GetQuestionsByLicenseClassUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudyUiState(licenseClassId = licenseClassId))
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<StudyUiEffect>()
    val effects: SharedFlow<StudyUiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch(dispatcher) {
            val questions = getQuestionsByLicenseClass(licenseClassId).filter { it.status == QuestionStatus.ACTIVE }
            _uiState.update { it.copy(isLoading = false, questions = questions, errorMessage = null) }
        }
    }

    fun onEvent(event: StudyUiEvent) {
        when (event) {
            is StudyUiEvent.SelectAnswer -> _uiState.update { it.copy(selectedOptionId = event.optionId) }
            StudyUiEvent.NextClicked -> _uiState.update { state ->
                val next = (state.currentIndex + 1).coerceAtMost((state.questions.size - 1).coerceAtLeast(0))
                state.copy(currentIndex = next, selectedOptionId = null)
            }
            StudyUiEvent.HomeClicked -> viewModelScope.launch(dispatcher) {
                _effects.emit(StudyUiEffect.Navigate(AppRoute.Home))
            }
        }
    }
}
