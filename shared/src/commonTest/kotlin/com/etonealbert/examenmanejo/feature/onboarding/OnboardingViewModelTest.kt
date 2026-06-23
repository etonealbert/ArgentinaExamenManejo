package com.etonealbert.examenmanejo.feature.onboarding

import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.repository.SettingsRepository
import com.etonealbert.examenmanejo.feature.assertNoReplayedEffect
import com.etonealbert.examenmanejo.feature.collectNextEffect
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertTrue

class OnboardingViewModelTest {
    @Test
    fun completeOnboardingPersistsSettingAndEmitsHomeRoute() {
        val settingsRepository = FakeSettingsRepository()
        val viewModel = OnboardingViewModel(
            settingsRepository = settingsRepository,
            dispatcher = Dispatchers.Unconfined,
        )
        val effects = collectNextEffect(viewModel.effects)

        viewModel.onEvent(OnboardingUiEvent.CompleteClicked)

        assertTrue(settingsRepository.completed)
        effects.assertSingleEffect(
            OnboardingUiEffect.Navigate(AppRoute.Home),
        )
        assertNoReplayedEffect(viewModel.effects)
    }

    private class FakeSettingsRepository : SettingsRepository {
        var completed = false

        override suspend fun hasCompletedOnboarding(): Boolean = completed

        override suspend fun setOnboardingCompleted(completed: Boolean) {
            this.completed = completed
        }

        override suspend fun getSelectedJurisdiction(): String? = null

        override suspend fun setSelectedJurisdiction(jurisdictionId: String) = Unit
    }
}
