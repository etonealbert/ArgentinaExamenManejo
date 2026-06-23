package com.etonealbert.examenmanejo.domain.model

data class StudyProgress(
    val questionId: String,
    val seenCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
)
