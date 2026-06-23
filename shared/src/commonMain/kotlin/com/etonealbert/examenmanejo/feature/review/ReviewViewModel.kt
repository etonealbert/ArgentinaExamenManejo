package com.etonealbert.examenmanejo.feature.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.usecase.GetReviewAnswersUseCase
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

class ReviewViewModel(
    private val examSessionId: Long,
    private val getReviewAnswers: GetReviewAnswersUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewUiState(examSessionId = examSessionId))
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ReviewUiEffect>()
    val effects: SharedFlow<ReviewUiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch(dispatcher) {
            val answers = getReviewAnswers(examSessionId).map { (snapshot, answer) ->
                ReviewAnswerUi(
                    questionText = snapshot.questionText,
                    explanation = snapshot.explanation,
                    options = snapshot.options.sortedBy { it.position }.map { option ->
                        ReviewOptionUi(
                            optionId = option.optionId,
                            text = option.text,
                            selected = answer?.selectedOptionId == option.optionId,
                            correct = snapshot.correctOptionId == option.optionId,
                        )
                    },
                )
            }
            _uiState.update { it.copy(isLoading = false, answers = answers) }
        }
    }

    fun onEvent(event: ReviewUiEvent) {
        val route = when (event) {
            ReviewUiEvent.HomeClicked -> AppRoute.Home
            ReviewUiEvent.HistoryClicked -> AppRoute.History
        }
        viewModelScope.launch(dispatcher) { _effects.emit(ReviewUiEffect.Navigate(route)) }
    }
}
