package com.etonealbert.examenmanejo.feature.review

import com.etonealbert.examenmanejo.core.navigation.AppRoute

sealed interface ReviewUiEffect {
    data class Navigate(val route: AppRoute) : ReviewUiEffect
}
