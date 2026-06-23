package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.repository.ExamRepository

class GetExamHistoryUseCase(
    private val examRepository: ExamRepository,
) {
    suspend operator fun invoke(): List<ExamResult> = examRepository.getExamHistory()
}
