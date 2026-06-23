package com.etonealbert.examenmanejo.network

class NoOpQuestionPackApi : QuestionPackApi {
    override suspend fun fetchManifest(): Result<Unit> = Result.success(Unit)
}
