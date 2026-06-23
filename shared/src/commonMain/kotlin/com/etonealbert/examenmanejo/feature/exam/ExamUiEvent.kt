package com.etonealbert.examenmanejo.feature.exam

sealed interface ExamUiEvent {
    data class SelectAnswer(val questionId: String, val optionId: String) : ExamUiEvent
    data object PreviousClicked : ExamUiEvent
    data object NextClicked : ExamUiEvent
    data object FinishClicked : ExamUiEvent
}
