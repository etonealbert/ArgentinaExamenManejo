package com.etonealbert.examenmanejo.network

interface QuestionPackApi {
    suspend fun fetchManifest(): Result<Unit>
}
