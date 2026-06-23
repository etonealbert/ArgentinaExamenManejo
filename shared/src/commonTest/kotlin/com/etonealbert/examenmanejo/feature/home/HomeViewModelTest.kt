package com.etonealbert.examenmanejo.feature.home

import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.model.LicenseClass
import com.etonealbert.examenmanejo.domain.repository.LicenseClassRepository
import com.etonealbert.examenmanejo.domain.usecase.GetLicenseClassesUseCase
import com.etonealbert.examenmanejo.feature.assertNoReplayedEffect
import com.etonealbert.examenmanejo.feature.collectNextEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test

class HomeViewModelTest {
    @Test
    fun startExamEmitsPrimitiveClassBExamRoute() {
        val viewModel = HomeViewModel(
            getLicenseClasses = GetLicenseClassesUseCase(FakeLicenseClassRepository()),
            dispatcher = Dispatchers.Unconfined,
        )
        val effects = collectNextEffect(viewModel.effects)

        viewModel.onEvent(HomeUiEvent.StartExamClicked("B"))

        effects.assertSingleEffect(
            HomeUiEffect.Navigate(AppRoute.Exam(licenseClassId = "B")),
        )
        assertNoReplayedEffect(viewModel.effects)
    }

    private class FakeLicenseClassRepository : LicenseClassRepository {
        override fun observeLicenseClasses(): Flow<List<LicenseClass>> = flowOf(
            listOf(
                LicenseClass(
                    id = "B",
                    displayName = "Clase B",
                    description = "Contenido demo local",
                    isEnabled = true,
                ),
            ),
        )
    }
}
