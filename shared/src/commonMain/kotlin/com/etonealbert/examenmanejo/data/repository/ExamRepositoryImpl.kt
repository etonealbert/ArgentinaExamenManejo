package com.etonealbert.examenmanejo.data.repository

import com.etonealbert.examenmanejo.data.local.ExamLocalDataSource
import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.model.Question
import com.etonealbert.examenmanejo.domain.repository.ExamRepository

class ExamRepositoryImpl(
    private val localDataSource: ExamLocalDataSource,
) : ExamRepository {
    override suspend fun startExam(config: ExamConfig, questions: List<Question>, startedAtEpochMillis: Long): Long =
        localDataSource.createSessionWithSnapshots(config, questions, startedAtEpochMillis)

    override suspend fun submitAnswer(
        sessionId: Long,
        questionId: String,
        selectedOptionId: String?,
        answeredAtEpochMillis: Long,
    ) {
        localDataSource.submitAnswer(sessionId, questionId, selectedOptionId, answeredAtEpochMillis)
    }

    override suspend fun getQuestionSnapshots(sessionId: Long): List<ExamQuestionSnapshot> =
        localDataSource.getQuestionSnapshots(sessionId)

    override suspend fun getAnswers(sessionId: Long): List<ExamAnswer> = localDataSource.getAnswers(sessionId)

    override suspend fun completeExam(result: ExamResult, completedAtEpochMillis: Long) {
        localDataSource.completeExam(result, completedAtEpochMillis)
    }

    override suspend fun getExamResult(sessionId: Long): ExamResult = localDataSource.getExamResult(sessionId)

    override suspend fun getExamHistory(): List<ExamResult> = localDataSource.getExamHistory()
}
