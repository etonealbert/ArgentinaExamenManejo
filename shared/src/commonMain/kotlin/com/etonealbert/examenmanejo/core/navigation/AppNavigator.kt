package com.etonealbert.examenmanejo.core.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AppNavigator {
    val currentRoute: StateFlow<AppRoute>
    fun navigate(route: AppRoute)
    fun replace(route: AppRoute)
    fun back()
}

class InMemoryAppNavigator(
    initialRoute: AppRoute = AppRoute.Onboarding,
) : AppNavigator {
    private val backStack = mutableListOf(initialRoute)
    private val mutableCurrentRoute = MutableStateFlow(initialRoute)

    override val currentRoute: StateFlow<AppRoute> = mutableCurrentRoute

    override fun navigate(route: AppRoute) {
        backStack += route
        mutableCurrentRoute.value = route
    }

    override fun replace(route: AppRoute) {
        if (backStack.isEmpty()) {
            backStack += route
        } else {
            backStack[backStack.lastIndex] = route
        }
        mutableCurrentRoute.value = route
    }

    override fun back() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            mutableCurrentRoute.value = backStack.last()
        }
    }
}
