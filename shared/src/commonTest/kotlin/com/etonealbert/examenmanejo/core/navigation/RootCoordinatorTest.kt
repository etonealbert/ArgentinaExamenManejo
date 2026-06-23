package com.etonealbert.examenmanejo.core.navigation

import com.etonealbert.examenmanejo.data.local.seed.ImportResult
import com.etonealbert.examenmanejo.domain.repository.QuestionPackRepository
import com.etonealbert.examenmanejo.domain.repository.SettingsRepository
import com.etonealbert.examenmanejo.domain.usecase.CheckFirstLaunchSeedImportUseCase
import com.etonealbert.examenmanejo.domain.usecase.ImportSeedQuestionsUseCase
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals

class RootCoordinatorTest {
    @Test
    fun startImportsSeedAndRoutesToOnboardingWhenOnboardingIsIncomplete() = runSuspend {
        val questionPackRepository = FakeQuestionPackRepository()
        val navigator = InMemoryAppNavigator(initialRoute = AppRoute.Home)

        val selectedRoute = RootCoordinator(
            navigator = navigator,
            checkFirstLaunchSeedImport = checkFirstLaunchSeedImportUseCase(questionPackRepository),
            settingsRepository = FakeSettingsRepository(hasCompletedOnboarding = false),
        ).start()

        assertEquals(1, questionPackRepository.importAttempts)
        assertEquals(AppRoute.Onboarding, selectedRoute)
        assertEquals(AppRoute.Onboarding, navigator.currentRoute.value)
    }

    @Test
    fun startImportsSeedAndRoutesToHomeWhenOnboardingIsComplete() = runSuspend {
        val questionPackRepository = FakeQuestionPackRepository()
        val navigator = InMemoryAppNavigator(initialRoute = AppRoute.Onboarding)

        val selectedRoute = RootCoordinator(
            navigator = navigator,
            checkFirstLaunchSeedImport = checkFirstLaunchSeedImportUseCase(questionPackRepository),
            settingsRepository = FakeSettingsRepository(hasCompletedOnboarding = true),
        ).start()

        assertEquals(1, questionPackRepository.importAttempts)
        assertEquals(AppRoute.Home, selectedRoute)
        assertEquals(AppRoute.Home, navigator.currentRoute.value)
    }

    private fun checkFirstLaunchSeedImportUseCase(
        questionPackRepository: QuestionPackRepository,
    ) = CheckFirstLaunchSeedImportUseCase(ImportSeedQuestionsUseCase(questionPackRepository))

    private class FakeQuestionPackRepository : QuestionPackRepository {
        var importAttempts = 0
            private set

        override suspend fun importBundledSeedIfNeeded(): ImportResult {
            importAttempts += 1
            return ImportResult.AlreadyImported
        }
    }

    private class FakeSettingsRepository(
        private val hasCompletedOnboarding: Boolean,
    ) : SettingsRepository {
        override suspend fun hasCompletedOnboarding(): Boolean = hasCompletedOnboarding

        override suspend fun setOnboardingCompleted(completed: Boolean) = error("Not used")

        override suspend fun getSelectedJurisdiction(): String? = error("Not used")

        override suspend fun setSelectedJurisdiction(jurisdictionId: String) = error("Not used")
    }

    private fun runSuspend(block: suspend () -> Unit) {
        var failure: Throwable? = null
        block.startCoroutine(object : Continuation<Unit> {
            override val context = EmptyCoroutineContext

            override fun resumeWith(result: Result<Unit>) {
                failure = result.exceptionOrNull()
            }
        })
        failure?.let { throw it }
    }
}
