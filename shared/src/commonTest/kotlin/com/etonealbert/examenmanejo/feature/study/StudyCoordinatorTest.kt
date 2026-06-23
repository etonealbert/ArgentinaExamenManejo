package com.etonealbert.examenmanejo.feature.study

import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.core.navigation.InMemoryAppNavigator
import kotlin.test.Test
import kotlin.test.assertEquals

class StudyCoordinatorTest {
    @Test
    fun opensStudyWithLicenseClassIdRoute() {
        val navigator = InMemoryAppNavigator(initialRoute = AppRoute.Home)

        StudyCoordinator(navigator).openStudy(licenseClassId = "B")

        assertEquals(AppRoute.Study(licenseClassId = "B"), navigator.currentRoute.value)
    }
}
