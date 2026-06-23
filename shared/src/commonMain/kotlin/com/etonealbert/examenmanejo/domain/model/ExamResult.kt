package com.etonealbert.examenmanejo.domain.model

data class ExamResult(
    val sessionId: Long,
    val licenseClassId: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val scorePercentage: Int,
    val passed: Boolean,
)
