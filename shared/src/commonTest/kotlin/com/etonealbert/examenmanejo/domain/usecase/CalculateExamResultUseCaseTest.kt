package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamOptionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculateExamResultUseCaseTest {
    private val config = ExamConfig(
        licenseClassId = "B",
        questionCount = 3,
        passingPercentage = 70,
        timeLimitMinutes = null,
    )

    private val snapshots = listOf(
        snapshot("q1", "q1-a"),
        snapshot("q2", "q2-a"),
        snapshot("q3", "q3-a"),
    )

    @Test
    fun allAnswersCorrectPasses() {
        val result = CalculateExamResultUseCase().calculate(
            config = config,
            snapshots = snapshots,
            answers = listOf(answer("q1", "q1-a"), answer("q2", "q2-a"), answer("q3", "q3-a")),
        )

        assertEquals(3, result.correctCount)
        assertEquals(0, result.incorrectCount)
        assertEquals(100, result.scorePercentage)
        assertTrue(result.passed)
    }

    @Test
    fun unansweredQuestionsCountAsIncorrect() {
        val result = CalculateExamResultUseCase().calculate(
            config = config,
            snapshots = snapshots,
            answers = listOf(answer("q1", "q1-a"), answer("q2", "q2-b")),
        )

        assertEquals(1, result.correctCount)
        assertEquals(2, result.incorrectCount)
        assertEquals(33, result.scorePercentage)
        assertFalse(result.passed)
    }

    private fun snapshot(questionId: String, correctOptionId: String) = ExamQuestionSnapshot(
        questionId = questionId,
        questionText = "Question $questionId",
        category = "Reglas de tránsito",
        explanation = "Explicación demo",
        options = listOf(
            ExamOptionSnapshot(optionId = correctOptionId, text = "Correcta", position = 0),
            ExamOptionSnapshot(optionId = questionId + "-b", text = "Incorrecta", position = 1),
        ),
        correctOptionId = correctOptionId,
        order = 0,
    )

    private fun answer(questionId: String, optionId: String) = ExamAnswer(
        sessionId = 1L,
        questionId = questionId,
        selectedOptionId = optionId,
        answeredAtEpochMillis = 1L,
    )
}
