package com.etonealbert.examenmanejo.feature.onboarding

sealed interface OnboardingUiEvent {
    data object CompleteClicked : OnboardingUiEvent
}
