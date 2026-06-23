package com.etonealbert.examenmanejo.core.navigation

sealed interface NavigationEffect {
    data class NavigateTo(val route: AppRoute) : NavigationEffect
    data class ReplaceWith(val route: AppRoute) : NavigationEffect
    data object NavigateBack : NavigationEffect
}
