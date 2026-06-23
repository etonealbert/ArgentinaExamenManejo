package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.core.time.Clock
import com.etonealbert.examenmanejo.domain.model.AnswerOption
import com.etonealbert.examenmanejo.domain.model.ContentStatus
import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.model.Question
import com.etonealbert.examenmanejo.domain.model.QuestionCategory
import com.etonealbert.examenmanejo.domain.model.QuestionSource
import com.etonealbert.examenmanejo.domain.model.QuestionStatus
import com.etonealbert.examenmanejo.domain.model.ReviewStatus
import com.etonealbert.examenmanejo.domain.repository.ExamRepository
import com.etonealbert.examenmanejo.domain.repository.QuestionRepository
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals

class StartExamUseCaseTest {
    @Test
    fun startsClassBExamWithConfiguredActiveQuestions() = runSuspend {
        val classBActive1 = question("b-1", listOf("B"), QuestionStatus.ACTIVE)
        val classBInactive = question("b-inactive", listOf("B"), QuestionStatus.INACTIVE)
        val classAActive = question("a-1", listOf("A"), QuestionStatus.ACTIVE)
        val classBActive2 = question("b-2", listOf("B"), QuestionStatus.ACTIVE)
        val questionRepository = FakeQuestionRepository(
            questionsByLicenseClass = mapOf(
                "B" to listOf(classBActive1, classBInactive, classBActive2),
                "A" to listOf(classAActive),
            ),
        )
        val examRepository = FakeExamRepository()
        val config = classBDemoExamConfig(questionCount = 2)

        val sessionId = StartExamUseCase(
            questionRepository = questionRepository,
            examRepository = examRepository,
            clock = FixedClock(nowEpochMillis = 1234L),
        )(config)

        assertEquals(99L, sessionId)
        assertEquals(listOf("B"), questionRepository.requestedLicenseClassIds)
        assertEquals(config, examRepository.startedConfig)
        assertEquals(70, examRepository.startedConfig?.passingPercentage)
        assertEquals(2, examRepository.startedConfig?.questionCount)
        assertEquals(1234L, examRepository.startedAtEpochMillis)
        assertEquals(listOf(classBActive1, classBActive2), examRepository.startedQuestions)
    }

    private class FakeQuestionRepository(
        private val questionsByLicenseClass: Map<String, List<Question>>,
    ) : QuestionRepository {
        val requestedLicenseClassIds = mutableListOf<String>()

        override suspend fun getQuestionsByLicenseClass(licenseClassId: String): List<Question> {
            requestedLicenseClassIds += licenseClassId
            return questionsByLicenseClass.getValue(licenseClassId)
        }
    }

    private class FakeExamRepository : ExamRepository {
        var startedConfig: ExamConfig? = null
            private set
        var startedQuestions: List<Question> = emptyList()
            private set
        var startedAtEpochMillis: Long? = null
            private set

        override suspend fun startExam(
            config: ExamConfig,
            questions: List<Question>,
            startedAtEpochMillis: Long,
        ): Long {
            startedConfig = config
            startedQuestions = questions
            this.startedAtEpochMillis = startedAtEpochMillis
            return 99L
        }

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

        override suspend fun getExamHistory(): List<ExamResult> = error("Not used")
    }

    private class FixedClock(private val nowEpochMillis: Long) : Clock {
        override fun nowEpochMillis(): Long = nowEpochMillis
    }

    private fun runSuspend(block: suspend () -> Unit) {
        var failure: Throwable? = null
        block.startCoroutine(object : Continuation<Unit> {
            override val context = EmptyCoroutineContext

            override fun resumeWith(result: Result<Unit>) {
                failure = result.exceptionOrNull()
            }
        })
        failure?.let { throw it }
    }

    private fun question(
        id: String,
        licenseClassIds: List<String>,
        status: QuestionStatus,
    ) = Question(
        id = id,
        licenseClassIds = licenseClassIds,
        jurisdictionId = "ar",
        category = QuestionCategory(id = "traffic-rules", displayName = "Reglas de transito"),
        source = QuestionSource(
            id = "demo",
            title = "Demo",
            url = "https://example.com",
            accessedAt = "2026-06-22",
            attribution = "Demo",
        ),
        text = "Question $id",
        explanation = "Demo explanation",
        options = listOf(
            AnswerOption(id = "$id-a", questionId = id, text = "Correct", isCorrect = true, position = 0),
            AnswerOption(id = "$id-b", questionId = id, text = "Incorrect", isCorrect = false, position = 1),
        ),
        contentStatus = ContentStatus.DEMO_UNVERIFIED,
        reviewStatus = ReviewStatus.APPROVED,
        status = status,
    )
}
