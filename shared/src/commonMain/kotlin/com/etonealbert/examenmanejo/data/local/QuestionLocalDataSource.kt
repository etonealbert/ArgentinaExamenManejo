package com.etonealbert.examenmanejo.data.local

import com.etonealbert.examenmanejo.data.mapper.toDomain
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase
import com.etonealbert.examenmanejo.domain.model.Question

class QuestionLocalDataSource(
    private val database: ExamenManejoDatabase,
) {
    suspend fun getQuestionsByLicenseClass(licenseClassId: String): List<Question> = database.contentQueries
        .selectQuestionsByLicenseClass(licenseClassId)
        .executeAsList()
        .map { row ->
            val options = database.contentQueries.selectQuestionOptions(row.question_id).executeAsList()
            val category = database.contentQueries.selectQuestionCategory(row.category_id).executeAsOne()
            val source = database.contentQueries.selectContentSource(row.source_id).executeAsOne()
            row.toDomain(
                options = options,
                category = category,
                source = source,
                licenseClassIds = listOf(licenseClassId),
            )
        }
}
