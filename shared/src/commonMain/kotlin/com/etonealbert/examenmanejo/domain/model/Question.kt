package com.etonealbert.examenmanejo.domain.model

data class Question(
    val id: String,
    val licenseClassIds: List<String>,
    val jurisdictionId: String,
    val category: QuestionCategory,
    val source: QuestionSource,
    val text: String,
    val explanation: String,
    val options: List<AnswerOption>,
    val contentStatus: ContentStatus,
    val reviewStatus: ReviewStatus,
    val status: QuestionStatus,
)
