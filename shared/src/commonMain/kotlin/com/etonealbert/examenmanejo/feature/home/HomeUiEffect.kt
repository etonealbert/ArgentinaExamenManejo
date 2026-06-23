package com.etonealbert.examenmanejo.feature.home

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface HomeUiEffect {
    data class Navigate(val route: AppRoute) : HomeUiEffect
}
