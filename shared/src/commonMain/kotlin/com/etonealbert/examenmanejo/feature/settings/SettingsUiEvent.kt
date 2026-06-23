package com.etonealbert.examenmanejo.feature.settings

sealed interface SettingsUiEvent {
    data object HomeClicked : SettingsUiEvent
}
