package com.etonealbert.examenmanejo.domain.model

data class ExamConfig(
    val licenseClassId: String,
    val questionCount: Int,
    val passingPercentage: Int,
    val timeLimitMinutes: Int?,
)
