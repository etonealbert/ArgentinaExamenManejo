package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.core.time.Clock
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.repository.ExamRepository

class FinishExamUseCase(
    private val examRepository: ExamRepository,
    private val calculate: CalculateExamResultUseCase,
    private val clock: Clock,
) {
    suspend operator fun invoke(sessionId: Long, config: ExamConfig): ExamResult {
        val result = calculate.calculate(
            config = config,
            snapshots = examRepository.getQuestionSnapshots(sessionId),
            answers = examRepository.getAnswers(sessionId),
            sessionId = sessionId,
        )
        examRepository.completeExam(result, clock.nowEpochMillis())
        return result
    }
}
