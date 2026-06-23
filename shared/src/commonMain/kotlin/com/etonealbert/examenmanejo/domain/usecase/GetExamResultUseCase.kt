package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.repository.ExamRepository

class GetExamResultUseCase(
    private val examRepository: ExamRepository,
) {
    suspend operator fun invoke(sessionId: Long): ExamResult = examRepository.getExamResult(sessionId)
}
