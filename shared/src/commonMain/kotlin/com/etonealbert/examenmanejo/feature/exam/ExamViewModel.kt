package com.etonealbert.examenmanejo.feature.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.usecase.FinishExamUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetReviewAnswersUseCase
import com.etonealbert.examenmanejo.domain.usecase.StartExamUseCase
import com.etonealbert.examenmanejo.domain.usecase.SubmitExamAnswerUseCase
import com.etonealbert.examenmanejo.domain.usecase.classBDemoExamConfig
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

class ExamViewModel(
    licenseClassId: String,
    private val startExam: StartExamUseCase,
    private val submitExamAnswer: SubmitExamAnswerUseCase,
    private val finishExam: FinishExamUseCase,
    private val getReviewAnswers: GetReviewAnswersUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val config = classBDemoExamConfig(questionCount = 10).copy(licenseClassId = licenseClassId)
    private val _uiState = MutableStateFlow(ExamUiState(licenseClassId = licenseClassId))
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ExamUiEffect>()
    val effects: SharedFlow<ExamUiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch(dispatcher) {
            val sessionId = startExam(config)
            val reviewItems = getReviewAnswers(sessionId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    sessionId = sessionId,
                    questions = reviewItems.map { item -> item.first }.sortedBy { snapshot -> snapshot.order },
                    selectedAnswers = reviewItems.mapNotNull { (snapshot, answer) ->
                        answer?.selectedOptionId?.let { selected -> snapshot.questionId to selected }
                    }.toMap(),
                )
            }
        }
    }

    fun onEvent(event: ExamUiEvent) {
        when (event) {
            is ExamUiEvent.SelectAnswer -> selectAnswer(event.questionId, event.optionId)
            ExamUiEvent.PreviousClicked -> _uiState.update { it.copy(currentIndex = (it.currentIndex - 1).coerceAtLeast(0)) }
            ExamUiEvent.NextClicked -> _uiState.update { state ->
                state.copy(currentIndex = (state.currentIndex + 1).coerceAtMost((state.questions.size - 1).coerceAtLeast(0)))
            }
            ExamUiEvent.FinishClicked -> finish()
        }
    }

    private fun selectAnswer(questionId: String, optionId: String) {
        _uiState.update { it.copy(selectedAnswers = it.selectedAnswers + (questionId to optionId)) }
        val sessionId = _uiState.value.sessionId ?: return
        viewModelScope.launch(dispatcher) {
            submitExamAnswer(sessionId, questionId, optionId)
        }
    }

    private fun finish() {
        val state = _uiState.value
        val sessionId = state.sessionId ?: return
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isFinishing = true) }
            state.selectedAnswers.forEach { (questionId, optionId) ->
                submitExamAnswer(sessionId, questionId, optionId)
            }
            val result = finishExam(sessionId, config)
            _uiState.update { it.copy(isFinishing = false) }
            _effects.emit(ExamUiEffect.Navigate(AppRoute.Result(result.sessionId)))
        }
    }
}
