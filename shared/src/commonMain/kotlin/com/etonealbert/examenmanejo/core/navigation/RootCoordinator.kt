package com.etonealbert.examenmanejo.core.navigation

import com.etonealbert.examenmanejo.domain.repository.SettingsRepository
import com.etonealbert.examenmanejo.domain.model.ImportResult
import com.etonealbert.examenmanejo.domain.usecase.CheckFirstLaunchSeedImportUseCase

class RootCoordinator(
    private val navigator: AppNavigator,
    private val checkFirstLaunchSeedImport: CheckFirstLaunchSeedImportUseCase,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun start(): AppRoute {
        val importResult = checkFirstLaunchSeedImport()
        if (importResult is ImportResult.Failed) {
            navigator.replace(AppRoute.StartupError)
            return AppRoute.StartupError
        }
        val route = if (settingsRepository.hasCompletedOnboarding()) {
            AppRoute.Home
        } else {
            AppRoute.Onboarding
        }
        navigator.replace(route)
        return route
    }
}
