package com.etonealbert.examenmanejo.feature.history

import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.model.Question
import com.etonealbert.examenmanejo.domain.repository.ExamRepository
import com.etonealbert.examenmanejo.domain.usecase.GetExamHistoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals

class HistoryViewModelTest {
    @Test
    fun preservesHistoryUseCaseOrdering() {
        val viewModel = HistoryViewModel(
            getExamHistory = GetExamHistoryUseCase(FakeExamRepository()),
            dispatcher = Dispatchers.Unconfined,
        )

        assertEquals(listOf(3L, 1L, 5L), viewModel.uiState.value.results.map { it.sessionId })
    }

    private class FakeExamRepository : ExamRepository {
        override suspend fun startExam(
            config: ExamConfig,
            questions: List<Question>,
            startedAtEpochMillis: Long,
        ): Long = error("Not used")

        override suspend fun submitAnswer(
            sessionId: Long,
            questionId: String,
            selectedOptionId: String?,
            answeredAtEpochMillis: Long,
        ) = error("Not used")

        override suspend fun getQuestionSnapshots(sessionId: Long): List<ExamQuestionSnapshot> = error("Not used")

        override suspend fun getAnswers(sessionId: Long): List<ExamAnswer> = error("Not used")

        override suspend fun completeExam(result: ExamResult, completedAtEpochMillis: Long) = error("Not used")

        override suspend fun getExamResult(sessionId: Long): ExamResult = error("Not used")

        override suspend fun getExamHistory(): List<ExamResult> = listOf(
            examResult(sessionId = 3L),
            examResult(sessionId = 1L),
            examResult(sessionId = 5L),
        )

        private fun examResult(sessionId: Long): ExamResult = ExamResult(
            sessionId = sessionId,
            licenseClassId = "B",
            totalQuestions = 20,
            correctCount = 17,
            incorrectCount = 3,
            scorePercentage = 85,
            passed = true,
        )
    }
}
