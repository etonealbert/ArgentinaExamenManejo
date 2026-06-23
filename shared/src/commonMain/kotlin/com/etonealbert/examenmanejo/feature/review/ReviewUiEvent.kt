package com.etonealbert.examenmanejo.feature.review

sealed interface ReviewUiEvent {
    data object HomeClicked : ReviewUiEvent
    data object HistoryClicked : ReviewUiEvent
}
