package com.etonealbert.examenmanejo.feature.exam

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface ExamUiEffect {
    data class Navigate(val route: AppRoute) : ExamUiEffect
}
