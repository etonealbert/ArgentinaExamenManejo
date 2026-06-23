package com.etonealbert.examenmanejo.feature.subscription

import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.core.navigation.InMemoryAppNavigator
import kotlin.test.Test
import kotlin.test.assertEquals

class SubscriptionCoordinatorTest {
    @Test
    fun opensPaywallWithSourceRoute() {
        val navigator = InMemoryAppNavigator(initialRoute = AppRoute.Home)

        SubscriptionCoordinator(navigator).openPaywall(source = "exam-limit")

        assertEquals(AppRoute.Paywall(source = "exam-limit"), navigator.currentRoute.value)
    }
}
