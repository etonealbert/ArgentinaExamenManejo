package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ImportResult

class CheckFirstLaunchSeedImportUseCase(
    private val importSeedQuestions: ImportSeedQuestionsUseCase,
) {
    suspend operator fun invoke(): ImportResult = importSeedQuestions()
}
