package com.etonealbert.examenmanejo.data.repository

import com.etonealbert.examenmanejo.data.local.HAS_COMPLETED_ONBOARDING
import com.etonealbert.examenmanejo.data.local.SELECTED_JURISDICTION
import com.etonealbert.examenmanejo.data.local.SettingsLocalDataSource
import com.etonealbert.examenmanejo.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource,
) : SettingsRepository {
    override suspend fun hasCompletedOnboarding(): Boolean =
        localDataSource.getString(HAS_COMPLETED_ONBOARDING).toBoolean()

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        localDataSource.setString(HAS_COMPLETED_ONBOARDING, completed.toString())
    }

    override suspend fun getSelectedJurisdiction(): String? = localDataSource.getString(SELECTED_JURISDICTION)

    override suspend fun setSelectedJurisdiction(jurisdictionId: String) {
        localDataSource.setString(SELECTED_JURISDICTION, jurisdictionId)
    }
}
