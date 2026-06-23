package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.Question
import com.etonealbert.examenmanejo.domain.repository.QuestionRepository

class GetQuestionsByLicenseClassUseCase(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(licenseClassId: String): List<Question> =
        repository.getQuestionsByLicenseClass(licenseClassId)
}
