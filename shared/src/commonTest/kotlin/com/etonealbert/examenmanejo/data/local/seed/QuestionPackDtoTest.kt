package com.etonealbert.examenmanejo.data.local.seed

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
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
}
