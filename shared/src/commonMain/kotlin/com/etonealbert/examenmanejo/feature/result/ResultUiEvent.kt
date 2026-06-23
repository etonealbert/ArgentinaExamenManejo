package com.etonealbert.examenmanejo.feature.result

sealed interface ResultUiEvent {
    data object ReviewClicked : ResultUiEvent
    data object HistoryClicked : ResultUiEvent
    data object HomeClicked : ResultUiEvent
}
