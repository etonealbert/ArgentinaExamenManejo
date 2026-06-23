package com.etonealbert.examenmanejo.feature.study

import com.etonealbert.examenmanejo.core.navigation.AppNavigator
import com.etonealbert.examenmanejo.core.navigation.AppRoute

class StudyCoordinator(
    private val navigator: AppNavigator,
) {
    fun openStudy(licenseClassId: String) {
        navigator.navigate(AppRoute.Study(licenseClassId = licenseClassId))
    }
}
