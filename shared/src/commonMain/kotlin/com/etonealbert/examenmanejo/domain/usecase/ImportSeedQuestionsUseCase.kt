package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.data.local.seed.ImportResult
import com.etonealbert.examenmanejo.domain.repository.QuestionPackRepository

class ImportSeedQuestionsUseCase(
    private val questionPackRepository: QuestionPackRepository,
) {
    suspend operator fun invoke(): ImportResult = questionPackRepository.importBundledSeedIfNeeded()
}
