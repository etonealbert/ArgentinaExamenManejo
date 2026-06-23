package com.etonealbert.examenmanejo.data.repository

import com.etonealbert.examenmanejo.data.local.LicenseClassLocalDataSource
import com.etonealbert.examenmanejo.domain.model.LicenseClass
import com.etonealbert.examenmanejo.domain.repository.LicenseClassRepository
import kotlinx.coroutines.flow.Flow

class LicenseClassRepositoryImpl(
    private val localDataSource: LicenseClassLocalDataSource,
) : LicenseClassRepository {
    override fun observeLicenseClasses(): Flow<List<LicenseClass>> = localDataSource.observeLicenseClasses()
}
