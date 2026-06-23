package com.etonealbert.examenmanejo.feature.history

sealed interface HistoryUiEvent {
    data class ResultClicked(val examSessionId: Long) : HistoryUiEvent
    data object HomeClicked : HistoryUiEvent
}
