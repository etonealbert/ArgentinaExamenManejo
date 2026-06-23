package com.etonealbert.examenmanejo.data.mapper

import com.etonealbert.examenmanejo.db.Content_source
import com.etonealbert.examenmanejo.db.Question as QuestionRow
import com.etonealbert.examenmanejo.db.Question_category
import com.etonealbert.examenmanejo.db.Question_option
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseMappersTest {
    @Test
    fun mapsQuestionMetadataFromCategoryAndSourceRows() {
        val domain = QuestionRow(
            question_id = "q1",
            pack_id = "pack",
            jurisdiction_id = "AR-DEMO",
            category_id = "traffic_rules",
            source_id = "demo-source",
            text = "Pregunta demo",
            explanation = "Explicacion demo",
            content_status = "DEMO_UNVERIFIED",
            review_status = "APPROVED",
            question_status = "ACTIVE",
            content_hash = "hash",
        ).toDomain(
            options = listOf(
                Question_option(
                    option_id = "q1-a",
                    question_id = "q1",
                    text = "Correcta",
                    is_correct = 1L,
                    position = 0L,
                ),
            ),
            category = Question_category(
                category_id = "traffic_rules",
                display_name = "Reglas de transito",
            ),
            source = Content_source(
                source_id = "demo-source",
                title = "Contenido demo no oficial",
                url = "local-demo",
                accessed_at = "2026-06-22",
                attribution = "Contenido original demo",
            ),
            licenseClassIds = listOf("B"),
        )

        assertEquals("Reglas de transito", domain.category.displayName)
        assertEquals("Contenido demo no oficial", domain.source.title)
        assertEquals("local-demo", domain.source.url)
        assertEquals("2026-06-22", domain.source.accessedAt)
        assertEquals("Contenido original demo", domain.source.attribution)
        assertEquals("Reglas de transito", domain.toExamQuestionSnapshot(order = 0).category)
    }
}
