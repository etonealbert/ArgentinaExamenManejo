package com.etonealbert.examenmanejo.feature.result

import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.repository.ExamRepository
import com.etonealbert.examenmanejo.domain.usecase.GetExamResultUseCase
import com.etonealbert.examenmanejo.feature.assertNoReplayedEffect
import com.etonealbert.examenmanejo.feature.collectNextEffect
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals

class ResultViewModelTest {
    @Test
    fun loadsPersistedResultAndEmitsPrimitiveReviewRoute() {
        val viewModel = ResultViewModel(
            examSessionId = 42L,
            getExamResult = GetExamResultUseCase(FakeExamRepository()),
            dispatcher = Dispatchers.Unconfined,
        )

        assertEquals(85, viewModel.uiState.value.result?.scorePercentage)

        val effects = collectNextEffect(viewModel.effects)
        viewModel.onEvent(ResultUiEvent.ReviewClicked)

        effects.assertSingleEffect(
            ResultUiEffect.Navigate(AppRoute.Review(examSessionId = 42L)),
        )
        assertNoReplayedEffect(viewModel.effects)
    }

    private class FakeExamRepository : ExamRepository {
        override suspend fun startExam(
            config: com.etonealbert.examenmanejo.domain.model.ExamConfig,
            questions: List<com.etonealbert.examenmanejo.domain.model.Question>,
            startedAtEpochMillis: Long,
        ): Long = error("Not used")

        override suspend fun submitAnswer(
            sessionId: Long,
            questionId: String,
            selectedOptionId: String?,
            answeredAtEpochMillis: Long,
        ) = error("Not used")

        override suspend fun getQuestionSnapshots(
            sessionId: Long,
        ): List<com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot> = error("Not used")

        override suspend fun getAnswers(
            sessionId: Long,
        ): List<com.etonealbert.examenmanejo.domain.model.ExamAnswer> = error("Not used")

        override suspend fun completeExam(
            result: ExamResult,
            completedAtEpochMillis: Long,
        ) = error("Not used")

        override suspend fun getExamResult(sessionId: Long): ExamResult = ExamResult(
            sessionId = sessionId,
            licenseClassId = "B",
            totalQuestions = 20,
            correctCount = 17,
            incorrectCount = 3,
            scorePercentage = 85,
            passed = true,
        )

        override suspend fun getExamHistory(): List<ExamResult> = error("Not used")
    }
}
