package com.etonealbert.examenmanejo.feature.exam

import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.core.time.Clock
import com.etonealbert.examenmanejo.domain.model.AnswerOption
import com.etonealbert.examenmanejo.domain.model.ContentStatus
import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamOptionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.model.Question
import com.etonealbert.examenmanejo.domain.model.QuestionCategory
import com.etonealbert.examenmanejo.domain.model.QuestionSource
import com.etonealbert.examenmanejo.domain.model.QuestionStatus
import com.etonealbert.examenmanejo.domain.model.ReviewStatus
import com.etonealbert.examenmanejo.domain.repository.ExamRepository
import com.etonealbert.examenmanejo.domain.repository.QuestionRepository
import com.etonealbert.examenmanejo.domain.usecase.CalculateExamResultUseCase
import com.etonealbert.examenmanejo.domain.usecase.FinishExamUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetReviewAnswersUseCase
import com.etonealbert.examenmanejo.domain.usecase.StartExamUseCase
import com.etonealbert.examenmanejo.domain.usecase.SubmitExamAnswerUseCase
import com.etonealbert.examenmanejo.feature.assertNoReplayedEffect
import com.etonealbert.examenmanejo.feature.collectNextEffect
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals

class ExamViewModelTest {
    @Test
    fun finishPersistsSelectedAnswerAndEmitsResultRouteWithSessionId() {
        val examRepository = FakeExamRepository()
        val clock = FixedClock()
        val viewModel = ExamViewModel(
            licenseClassId = "B",
            startExam = StartExamUseCase(FakeQuestionRepository(), examRepository, clock),
            submitExamAnswer = SubmitExamAnswerUseCase(examRepository, clock),
            finishExam = FinishExamUseCase(examRepository, CalculateExamResultUseCase(), clock),
            getReviewAnswers = GetReviewAnswersUseCase(examRepository),
            dispatcher = Dispatchers.Unconfined,
        )
        val effects = collectNextEffect(viewModel.effects)

        viewModel.onEvent(ExamUiEvent.SelectAnswer("q1", "a1"))
        viewModel.onEvent(ExamUiEvent.FinishClicked)

        assertEquals(42L, examRepository.completedResult?.sessionId)
        assertEquals("a1", examRepository.answers.single().selectedOptionId)
        effects.assertSingleEffect(
            ExamUiEffect.Navigate(AppRoute.Result(examSessionId = 42L)),
        )
        assertNoReplayedEffect(viewModel.effects)
    }

    private class FixedClock : Clock {
        override fun nowEpochMillis(): Long = 123L
    }

    private class FakeQuestionRepository : QuestionRepository {
        override suspend fun getQuestionsByLicenseClass(licenseClassId: String): List<Question> = listOf(
            Question(
                id = "q1",
                licenseClassIds = listOf(licenseClassId),
                jurisdictionId = "CL",
                category = QuestionCategory("signals", "Senales"),
                source = QuestionSource("demo", "DEMO_UNVERIFIED", "", "", "Demo local"),
                text = "Pregunta demo",
                explanation = "Explicacion demo",
                options = listOf(
                    AnswerOption("a1", "q1", "Correcta", isCorrect = true, position = 0),
                    AnswerOption("a2", "q1", "Incorrecta", isCorrect = false, position = 1),
                ),
                contentStatus = ContentStatus.DEMO_UNVERIFIED,
                reviewStatus = ReviewStatus.NEEDS_REVIEW,
                status = QuestionStatus.ACTIVE,
            ),
        )
    }

    private class FakeExamRepository : ExamRepository {
        val answers = mutableListOf<ExamAnswer>()
        var completedResult: ExamResult? = null
        private val snapshots = mutableListOf<ExamQuestionSnapshot>()

        override suspend fun startExam(
            config: ExamConfig,
            questions: List<Question>,
            startedAtEpochMillis: Long,
        ): Long {
            snapshots += questions.mapIndexed { index, question ->
                ExamQuestionSnapshot(
                    questionId = question.id,
                    questionText = question.text,
                    category = question.category.displayName,
                    explanation = question.explanation,
                    options = question.options.map { option ->
                        ExamOptionSnapshot(option.optionId(), option.text, option.position)
                    },
                    correctOptionId = question.options.first { it.isCorrect }.id,
                    order = index,
                )
            }
            return 42L
        }

        override suspend fun submitAnswer(
            sessionId: Long,
            questionId: String,
            selectedOptionId: String?,
            answeredAtEpochMillis: Long,
        ) {
            answers.removeAll { it.questionId == questionId }
            answers += ExamAnswer(sessionId, questionId, selectedOptionId, answeredAtEpochMillis)
        }

        override suspend fun getQuestionSnapshots(sessionId: Long): List<ExamQuestionSnapshot> = snapshots

        override suspend fun getAnswers(sessionId: Long): List<ExamAnswer> = answers

        override suspend fun completeExam(result: ExamResult, completedAtEpochMillis: Long) {
            completedResult = result
        }

        override suspend fun getExamResult(sessionId: Long): ExamResult = completedResult ?: error("No result")

        override suspend fun getExamHistory(): List<ExamResult> = completedResult?.let(::listOf).orEmpty()

        private fun AnswerOption.optionId(): String = id
    }
}
