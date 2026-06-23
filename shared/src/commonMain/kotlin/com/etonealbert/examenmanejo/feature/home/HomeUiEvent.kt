package com.etonealbert.examenmanejo.feature.home

sealed interface HomeUiEvent {
    data class StartExamClicked(val licenseClassId: String) : HomeUiEvent
    data class StudyClicked(val licenseClassId: String) : HomeUiEvent
    data object HistoryClicked : HomeUiEvent
    data object SettingsClicked : HomeUiEvent
}
