package com.etonealbert.examenmanejo.domain.model

data class AnswerOption(
    val id: String,
    val questionId: String,
    val text: String,
    val isCorrect: Boolean,
    val position: Int,
)
