package com.etonealbert.examenmanejo.data.local.seed

import examenmanejo.shared.generated.resources.Res
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class SeedQuestionPackProvider(
    private val json: Json = Json { ignoreUnknownKeys = false },
    private val readBytes: suspend (String) -> ByteArray = ::readSeedResourceBytes,
) {
    suspend fun readBundledClassBDemoPack(): QuestionPackDto {
        val rawBytes = readBytes(CLASS_B_DEMO_JSON_PATH)
        val raw = rawBytes.decodeToString()
        val expectedHash = readBytes(CLASS_B_DEMO_CHECKSUM_PATH).decodeToString().trim().uppercase()
        val actualHash = rawBytes.sha256HexUppercase()
        val pack = json.decodeFromString<QuestionPackDto>(raw)
        check(actualHash == expectedHash) {
            "Seed question pack checksum mismatch for ${pack.packId} v${pack.version}"
        }
        return pack.copy(hash = actualHash)
    }

    private companion object {
        const val CLASS_B_DEMO_JSON_PATH = "files/question_packs/argentina-class-b-demo-0001.json"
        const val CLASS_B_DEMO_CHECKSUM_PATH = "files/question_packs/argentina-class-b-demo-0001.sha256"
    }
}

@OptIn(ExperimentalResourceApi::class)
private suspend fun readSeedResourceBytes(path: String): ByteArray = Res.readBytes(path)
