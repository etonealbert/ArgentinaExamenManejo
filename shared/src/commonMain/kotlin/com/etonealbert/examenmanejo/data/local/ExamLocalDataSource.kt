package com.etonealbert.examenmanejo.data.local

import com.etonealbert.examenmanejo.data.mapper.toDatabaseLong
import com.etonealbert.examenmanejo.data.mapper.toDomain
import com.etonealbert.examenmanejo.data.mapper.toDomainResult
import com.etonealbert.examenmanejo.data.mapper.toExamQuestionSnapshot
import com.etonealbert.examenmanejo.data.mapper.toSnapshotJson
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase
import com.etonealbert.examenmanejo.db.Exam_session
import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.model.Question

class ExamLocalDataSource(
    private val database: ExamenManejoDatabase,
) {
    fun createSessionWithSnapshots(
        config: ExamConfig,
        questions: List<Question>,
        startedAtEpochMillis: Long,
    ): Long {
        var sessionId = 0L

        database.transaction {
            database.examQueries.insertExamSession(
                license_class_id = config.licenseClassId,
                status = "IN_PROGRESS",
                question_count = questions.size.toLong(),
                passing_percentage = config.passingPercentage.toLong(),
                started_at_epoch_ms = startedAtEpochMillis,
                completed_at_epoch_ms = null,
                score_percentage = null,
                passed = null,
            )
            sessionId = database.examQueries.lastInsertedSessionId().executeAsOne()

            questions.forEachIndexed { index, question ->
                val snapshot = question.toExamQuestionSnapshot(order = index)
                database.examQueries.insertQuestionSnapshot(
                    session_id = sessionId,
                    question_id = snapshot.questionId,
                    question_order = snapshot.order.toLong(),
                    question_text_snapshot = snapshot.questionText,
                    category_snapshot = snapshot.category,
                    explanation_snapshot = snapshot.explanation,
                    options_snapshot_json = snapshot.options.toSnapshotJson(),
                    correct_option_id_snapshot = snapshot.correctOptionId,
                )
            }
        }

        return sessionId
    }

    fun submitAnswer(
        sessionId: Long,
        questionId: String,
        selectedOptionId: String?,
        answeredAtEpochMillis: Long,
    ) {
        database.examQueries.upsertExamAnswer(
            session_id = sessionId,
            question_id = questionId,
            selected_option_id_snapshot = selectedOptionId,
            answered_at_epoch_ms = answeredAtEpochMillis,
        )
    }

    fun getQuestionSnapshots(sessionId: Long): List<ExamQuestionSnapshot> = database.examQueries
        .selectQuestionSnapshots(sessionId)
        .executeAsList()
        .map { it.toDomain() }

    fun getAnswers(sessionId: Long): List<ExamAnswer> = database.examQueries
        .selectExamAnswers(sessionId)
        .executeAsList()
        .map { it.toDomain() }

    fun completeExam(result: ExamResult, completedAtEpochMillis: Long) {
        database.examQueries.completeExamSession(
            completed_at_epoch_ms = completedAtEpochMillis,
            score_percentage = result.scorePercentage.toLong(),
            passed = result.passed.toDatabaseLong(),
            session_id = result.sessionId,
        )
    }

    fun getSession(sessionId: Long): Exam_session = database.examQueries.selectExamSession(sessionId).executeAsOne()

    fun getExamResult(sessionId: Long): ExamResult = getSession(sessionId).toDomainResult(
        snapshots = getQuestionSnapshots(sessionId),
        answers = getAnswers(sessionId),
    )

    fun getExamHistory(): List<ExamResult> = database.examQueries
        .selectCompletedExamHistory()
        .executeAsList()
        .map { session ->
            session.toDomainResult(
                snapshots = getQuestionSnapshots(session.session_id),
                answers = getAnswers(session.session_id),
            )
        }
}
