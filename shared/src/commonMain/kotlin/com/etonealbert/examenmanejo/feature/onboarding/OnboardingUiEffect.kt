package com.etonealbert.examenmanejo.feature.onboarding

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface OnboardingUiEffect {
    data class Navigate(val route: AppRoute) : OnboardingUiEffect
}
