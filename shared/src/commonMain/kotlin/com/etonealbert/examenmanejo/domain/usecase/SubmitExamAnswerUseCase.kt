package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.core.time.Clock
import com.etonealbert.examenmanejo.domain.repository.ExamRepository

class SubmitExamAnswerUseCase(
    private val examRepository: ExamRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(sessionId: Long, questionId: String, selectedOptionId: String?) {
        examRepository.submitAnswer(
            sessionId = sessionId,
            questionId = questionId,
            selectedOptionId = selectedOptionId,
            answeredAtEpochMillis = clock.nowEpochMillis(),
        )
    }
}
