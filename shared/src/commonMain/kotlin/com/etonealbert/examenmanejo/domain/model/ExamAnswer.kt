package com.etonealbert.examenmanejo.domain.model

data class ExamAnswer(
    val sessionId: Long,
    val questionId: String,
    val selectedOptionId: String?,
    val answeredAtEpochMillis: Long,
)
