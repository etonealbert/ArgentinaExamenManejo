package com.etonealbert.examenmanejo.domain.repository

interface SettingsRepository {
    suspend fun hasCompletedOnboarding(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun getSelectedJurisdiction(): String?
    suspend fun setSelectedJurisdiction(jurisdictionId: String)
}
