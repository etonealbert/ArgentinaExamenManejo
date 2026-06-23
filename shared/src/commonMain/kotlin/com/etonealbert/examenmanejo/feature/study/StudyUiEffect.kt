package com.etonealbert.examenmanejo.feature.study

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface StudyUiEffect {
    data class Navigate(val route: AppRoute) : StudyUiEffect
}
