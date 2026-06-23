package com.etonealbert.examenmanejo.feature.study

sealed interface StudyUiEvent {
    data class SelectAnswer(val optionId: String) : StudyUiEvent
    data object NextClicked : StudyUiEvent
    data object HomeClicked : StudyUiEvent
}
