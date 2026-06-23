package com.etonealbert.examenmanejo.feature.subscription

import com.etonealbert.examenmanejo.core.navigation.AppNavigator
import com.etonealbert.examenmanejo.core.navigation.AppRoute

class SubscriptionCoordinator(
    private val navigator: AppNavigator,
) {
    fun openPaywall(source: String) {
        navigator.navigate(AppRoute.Paywall(source = source))
    }
}
