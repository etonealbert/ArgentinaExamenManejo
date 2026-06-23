package com.etonealbert.examenmanejo.feature.settings

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface SettingsUiEffect {
    data class Navigate(val route: AppRoute) : SettingsUiEffect
}
