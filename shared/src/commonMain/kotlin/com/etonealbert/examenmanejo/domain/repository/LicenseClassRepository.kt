package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.domain.model.LicenseClass
import kotlinx.coroutines.flow.Flow

interface LicenseClassRepository {
    fun observeLicenseClasses(): Flow<List<LicenseClass>>
}
