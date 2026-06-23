package com.etonealbert.examenmanejo.feature.exam

import com.etonealbert.examenmanejo.core.navigation.AppNavigator
import com.etonealbert.examenmanejo.core.navigation.AppRoute

class ExamCoordinator(
    private val navigator: AppNavigator,
) {
    fun openExam(licenseClassId: String) {
        navigator.navigate(AppRoute.Exam(licenseClassId = licenseClassId))
    }

    fun openResult(examSessionId: Long) {
        navigator.navigate(AppRoute.Result(examSessionId = examSessionId))
    }

    fun openReview(examSessionId: Long) {
        navigator.navigate(AppRoute.Review(examSessionId = examSessionId))
    }

    fun closeToHome() {
        navigator.replace(AppRoute.Home)
    }

    fun openHistory() {
        navigator.navigate(AppRoute.History)
    }
}
