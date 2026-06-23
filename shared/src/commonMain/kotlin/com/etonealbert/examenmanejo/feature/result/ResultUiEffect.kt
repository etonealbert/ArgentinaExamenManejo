package com.etonealbert.examenmanejo.feature.result

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface ResultUiEffect {
    data class Navigate(val route: AppRoute) : ResultUiEffect
}
