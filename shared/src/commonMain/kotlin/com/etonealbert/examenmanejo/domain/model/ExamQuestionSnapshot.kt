package com.etonealbert.examenmanejo.domain.model

data class ExamQuestionSnapshot(
    val questionId: String,
    val questionText: String,
    val category: String,
    val explanation: String,
    val options: List<ExamOptionSnapshot>,
    val correctOptionId: String,
    val order: Int,
)
