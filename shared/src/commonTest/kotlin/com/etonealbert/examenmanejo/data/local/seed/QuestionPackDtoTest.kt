package com.etonealbert.examenmanejo.data.local.seed

import kotlinx.serialization.json.Json
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class QuestionPackDtoTest {
    @Test
    fun decodesDemoQuestionPack() {
        val pack = Json { ignoreUnknownKeys = false }.decodeFromString<QuestionPackDto>(demoJson)

        assertEquals("argentina-class-b-demo", pack.packId)
        assertEquals(1, pack.version)
        assertEquals("B", pack.licenseClasses.single().id)
        assertTrue(pack.questions.all { it.contentStatus == "DEMO_UNVERIFIED" })
        assertTrue(pack.questions.all { it.reviewStatus == "APPROVED" })
        assertTrue(pack.questions.all { it.questionStatus == "ACTIVE" })
        assertTrue(pack.questions.all { it.jurisdictionId == "AR-DEMO" })
        assertTrue(pack.questions.all { it.licenseClassIds == listOf("B") })
    }

    @Test
    fun sha256HexUppercaseUsesKnownVector() {
        assertEquals(
            "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD",
            "abc".encodeToByteArray().sha256HexUppercase(),
        )
    }

    @Test
    fun bundledPackReadUsesSha256FileAsPackHash() = runSuspend {
        val expectedHash = demoJson.encodeToByteArray().sha256HexUppercase()
        val provider = SeedQuestionPackProvider(
            readBytes = { path ->
                when {
                    path.endsWith(".json") -> demoJson.encodeToByteArray()
                    path.endsWith(".sha256") -> "$expectedHash\n".encodeToByteArray()
                    else -> error("Unexpected resource path: $path")
                }
            },
        )

        val pack = provider.readBundledClassBDemoPack()

        assertEquals(expectedHash, pack.hash)
    }

    @Test
    fun bundledPackReadFailsWhenChecksumResourceDiffersFromJsonContent() = runSuspend {
        val provider = SeedQuestionPackProvider(
            readBytes = { path ->
                when {
                    path.endsWith(".json") -> demoJson.encodeToByteArray()
                    path.endsWith(".sha256") -> "different-checksum\n".encodeToByteArray()
                    else -> error("Unexpected resource path: $path")
                }
            },
        )

        val failure = assertFailsWith<IllegalStateException> {
            runSuspend { provider.readBundledClassBDemoPack() }
        }

        assertTrue(failure.message.orEmpty().contains("checksum"))
    }

    private val demoJson = """
        {
          "packId":"argentina-class-b-demo",
          "version":1,
          "hash":"dev-hash",
          "title":"Preguntas demo Clase B",
          "sources":[{"id":"demo-source","title":"Contenido demo no oficial","url":"local-demo","accessedAt":"2026-06-22","attribution":"Contenido original demo para validar el flujo; no es material oficial."}],
          "jurisdictions":[{"id":"AR-DEMO","displayName":"Argentina demo","level":"demo"}],
          "licenseClasses":[{"id":"B","displayName":"Clase B","description":"Autos particulares demo","isEnabled":true}],
          "categories":[{"id":"traffic_rules","displayName":"Reglas de transito"}],
          "questions":[{"id":"AR-DEMO-B-000001","licenseClassIds":["B"],"jurisdictionId":"AR-DEMO","categoryId":"traffic_rules","sourceId":"demo-source","text":"Pregunta demo","explanation":"Explicacion demo","contentStatus":"DEMO_UNVERIFIED","reviewStatus":"APPROVED","questionStatus":"ACTIVE","contentHash":"q1","options":[{"id":"AR-DEMO-B-000001-A","text":"Correcta","isCorrect":true,"position":0},{"id":"AR-DEMO-B-000001-B","text":"Incorrecta","isCorrect":false,"position":1}]}]
        }
    """.trimIndent()

    private fun runSuspend(block: suspend () -> Unit) {
        var failure: Throwable? = null
        block.startCoroutine(object : Continuation<Unit> {
            override val context = EmptyCoroutineContext

            override fun resumeWith(result: Result<Unit>) {
                failure = result.exceptionOrNull()
            }
        })
        failure?.let { throw it }
    }
}
