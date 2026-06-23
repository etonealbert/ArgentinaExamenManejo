package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.data.local.seed.ImportResult

interface QuestionPackRepository {
    suspend fun importBundledSeedIfNeeded(): ImportResult
}
