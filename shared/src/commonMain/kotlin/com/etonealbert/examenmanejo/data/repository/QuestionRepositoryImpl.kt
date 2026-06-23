package com.etonealbert.examenmanejo.data.repository

import com.etonealbert.examenmanejo.data.local.QuestionLocalDataSource
import com.etonealbert.examenmanejo.domain.model.Question
import com.etonealbert.examenmanejo.domain.repository.QuestionRepository

class QuestionRepositoryImpl(
    private val localDataSource: QuestionLocalDataSource,
) : QuestionRepository {
    override suspend fun getQuestionsByLicenseClass(licenseClassId: String): List<Question> =
        localDataSource.getQuestionsByLicenseClass(licenseClassId)
}
