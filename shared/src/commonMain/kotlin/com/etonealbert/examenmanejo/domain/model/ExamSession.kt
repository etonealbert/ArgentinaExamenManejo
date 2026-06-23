package com.etonealbert.examenmanejo.domain.model

data class ExamSession(
    val id: Long,
    val licenseClassId: String,
    val status: String,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
)
