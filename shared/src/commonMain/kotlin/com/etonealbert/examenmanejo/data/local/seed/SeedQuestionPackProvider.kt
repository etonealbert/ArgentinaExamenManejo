package com.etonealbert.examenmanejo.data.local.seed

import examenmanejo.shared.generated.resources.Res
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class SeedQuestionPackProvider(
    private val json: Json = Json { ignoreUnknownKeys = false },
) {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun readBundledClassBDemoPack(): QuestionPackDto {
        val raw = Res.readBytes("files/question_packs/argentina-class-b-demo-0001.json").decodeToString()
        return json.decodeFromString(raw)
    }
}
