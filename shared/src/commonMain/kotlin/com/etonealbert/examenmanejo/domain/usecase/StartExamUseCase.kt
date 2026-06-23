package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.core.time.Clock
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.QuestionStatus
import com.etonealbert.examenmanejo.domain.repository.ExamRepository
import com.etonealbert.examenmanejo.domain.repository.QuestionRepository

class StartExamUseCase(
    private val questionRepository: QuestionRepository,
    private val examRepository: ExamRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(config: ExamConfig): Long {
        val questions = questionRepository.getQuestionsByLicenseClass(config.licenseClassId)
            .filter { it.status == QuestionStatus.ACTIVE }
            .take(config.questionCount)
        return examRepository.startExam(
            config = config,
            questions = questions,
            startedAtEpochMillis = clock.nowEpochMillis(),
        )
    }
}
