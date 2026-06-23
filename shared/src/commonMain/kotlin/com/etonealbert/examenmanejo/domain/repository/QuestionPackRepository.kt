package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.domain.model.ImportResult

interface QuestionPackRepository {
    suspend fun importBundledSeedIfNeeded(): ImportResult
}
