package com.etonealbert.examenmanejo.core.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryAppNavigatorTest {
    @Test
    fun navigateReplaceAndBackUpdateCurrentRoute() {
        val navigator = InMemoryAppNavigator(initialRoute = AppRoute.Home)

        navigator.navigate(AppRoute.Study(licenseClassId = "B"))
        navigator.navigate(AppRoute.Exam(licenseClassId = "B"))
        navigator.back()

        assertEquals(AppRoute.Study(licenseClassId = "B"), navigator.currentRoute.value)

        navigator.replace(AppRoute.Result(examSessionId = 42L))
        navigator.back()

        assertEquals(AppRoute.Home, navigator.currentRoute.value)

        navigator.back()

        assertEquals(AppRoute.Home, navigator.currentRoute.value)
    }
}
