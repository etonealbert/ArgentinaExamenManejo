package com.etonealbert.examenmanejo.feature.exam

import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.core.navigation.InMemoryAppNavigator
import kotlin.test.Test
import kotlin.test.assertEquals

class ExamCoordinatorTest {
    @Test
    fun mapsExamActionsToPrimitiveRoutes() {
        val navigator = InMemoryAppNavigator(initialRoute = AppRoute.Home)
        val coordinator = ExamCoordinator(navigator)

        coordinator.openExam(licenseClassId = "B")
        assertEquals(AppRoute.Exam(licenseClassId = "B"), navigator.currentRoute.value)

        coordinator.openResult(examSessionId = 7L)
        assertEquals(AppRoute.Result(examSessionId = 7L), navigator.currentRoute.value)

        coordinator.openReview(examSessionId = 7L)
        assertEquals(AppRoute.Review(examSessionId = 7L), navigator.currentRoute.value)

        coordinator.openHistory()
        assertEquals(AppRoute.History, navigator.currentRoute.value)

        coordinator.closeToHome()
        assertEquals(AppRoute.Home, navigator.currentRoute.value)
    }
}
