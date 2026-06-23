package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.LicenseClass
import com.etonealbert.examenmanejo.domain.repository.LicenseClassRepository
import kotlinx.coroutines.flow.Flow

class GetLicenseClassesUseCase(
    private val repository: LicenseClassRepository,
) {
    operator fun invoke(): Flow<List<LicenseClass>> = repository.observeLicenseClasses()
}
