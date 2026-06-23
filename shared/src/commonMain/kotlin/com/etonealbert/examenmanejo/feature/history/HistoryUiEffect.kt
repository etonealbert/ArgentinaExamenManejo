package com.etonealbert.examenmanejo.feature.history

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface HistoryUiEffect {
    data class Navigate(val route: AppRoute) : HistoryUiEffect
}
