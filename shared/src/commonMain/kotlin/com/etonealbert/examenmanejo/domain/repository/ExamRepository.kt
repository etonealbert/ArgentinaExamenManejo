package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.model.Question

interface ExamRepository {
    suspend fun startExam(config: ExamConfig, questions: List<Question>, startedAtEpochMillis: Long): Long
    suspend fun submitAnswer(sessionId: Long, questionId: String, selectedOptionId: String?, answeredAtEpochMillis: Long)
    suspend fun getQuestionSnapshots(sessionId: Long): List<ExamQuestionSnapshot>
    suspend fun getAnswers(sessionId: Long): List<ExamAnswer>
    suspend fun completeExam(result: ExamResult, completedAtEpochMillis: Long)
    suspend fun getExamResult(sessionId: Long): ExamResult
    suspend fun getExamHistory(): List<ExamResult>
}
