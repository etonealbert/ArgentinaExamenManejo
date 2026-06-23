package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.domain.model.Question

interface QuestionRepository {
    suspend fun getQuestionsByLicenseClass(licenseClassId: String): List<Question>
}
