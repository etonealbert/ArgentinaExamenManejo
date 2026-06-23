package com.etonealbert.examenmanejo.core.navigation

sealed interface AppRoute {
    data object Onboarding : AppRoute
    data object Home : AppRoute
    data object StartupError : AppRoute
    data class Study(val licenseClassId: String) : AppRoute
    data class Exam(val licenseClassId: String) : AppRoute
    data class Result(val examSessionId: Long) : AppRoute
    data class Review(val examSessionId: Long) : AppRoute
    data object History : AppRoute
    data object Settings : AppRoute
    data class Paywall(val source: String) : AppRoute
    data class Legal(val documentId: String) : AppRoute
    data class Tutorial(val source: String) : AppRoute
}
