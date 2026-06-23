package com.etonealbert.examenmanejo.data.local

import com.etonealbert.examenmanejo.data.mapper.toDatabaseLong
import com.etonealbert.examenmanejo.data.local.seed.QuestionPackDto
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase

class QuestionPackLocalDataSource(
    private val database: ExamenManejoDatabase,
) {
    fun selectQuestionPackHash(packId: String, version: Int): String? = database.contentQueries
        .selectQuestionPack(packId, version.toLong())
        .executeAsOneOrNull()
        ?.hash

    fun insertImportLog(
        importLogId: String,
        packId: String,
        version: Int,
        startedAtEpochMillis: Long,
        status: String,
        message: String,
    ) {
        database.contentQueries.insertImportLog(
            import_log_id = importLogId,
            pack_id = packId,
            version = version.toLong(),
            started_at_epoch_ms = startedAtEpochMillis,
            finished_at_epoch_ms = null,
            status = status,
            message = message,
        )
    }

    fun finishImportLog(
        importLogId: String,
        finishedAtEpochMillis: Long,
        status: String,
        message: String,
    ) {
        database.contentQueries.finishImportLog(
            finished_at_epoch_ms = finishedAtEpochMillis,
            status = status,
            message = message,
            import_log_id = importLogId,
        )
    }

    fun importContent(pack: QuestionPackDto, installedAtEpochMillis: Long): Int {
        database.transaction {
            pack.sources.forEach { source ->
                database.contentQueries.upsertContentSource(
                    source_id = source.id,
                    title = source.title,
                    url = source.url,
                    accessed_at = source.accessedAt,
                    attribution = source.attribution,
                )
            }

            pack.jurisdictions.forEach { jurisdiction ->
                database.contentQueries.upsertJurisdiction(
                    jurisdiction_id = jurisdiction.id,
                    display_name = jurisdiction.displayName,
                    level = jurisdiction.level,
                )
            }

            pack.licenseClasses.forEach { licenseClass ->
                database.contentQueries.upsertLicenseClass(
                    license_class_id = licenseClass.id,
                    display_name = licenseClass.displayName,
                    description = licenseClass.description,
                    is_enabled = licenseClass.isEnabled.toDatabaseLong(),
                )
            }

            pack.categories.forEach { category ->
                database.contentQueries.upsertQuestionCategory(
                    category_id = category.id,
                    display_name = category.displayName,
                )
            }

            pack.questions.forEach { question ->
                database.contentQueries.upsertQuestion(
                    question_id = question.id,
                    pack_id = pack.packId,
                    jurisdiction_id = question.jurisdictionId,
                    category_id = question.categoryId,
                    source_id = question.sourceId,
                    text = question.text,
                    explanation = question.explanation,
                    content_status = question.contentStatus,
                    review_status = question.reviewStatus,
                    question_status = question.questionStatus,
                    content_hash = question.contentHash,
                )

                database.contentQueries.deleteQuestionLicenseClasses(question.id)
                question.licenseClassIds.forEach { licenseClassId ->
                    database.contentQueries.insertQuestionLicenseClass(
                        question_id = question.id,
                        license_class_id = licenseClassId,
                    )
                }

                database.contentQueries.deleteQuestionOptions(question.id)
                question.options.forEach { option ->
                    database.contentQueries.upsertQuestionOption(
                        option_id = option.id,
                        question_id = question.id,
                        text = option.text,
                        is_correct = option.isCorrect.toDatabaseLong(),
                        position = option.position.toLong(),
                    )
                }
            }

            database.contentQueries.insertQuestionPack(
                pack_id = pack.packId,
                version = pack.version.toLong(),
                hash = pack.hash,
                title = pack.title,
                installed_at_epoch_ms = installedAtEpochMillis,
            )
        }

        return pack.questions.size
    }
}
