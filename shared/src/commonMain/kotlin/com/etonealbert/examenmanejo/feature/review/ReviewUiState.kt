package com.etonealbert.examenmanejo.feature.review

data class ReviewUiState(
    val examSessionId: Long,
    val isLoading: Boolean = true,
    val answers: List<ReviewAnswerUi> = emptyList(),
    val errorMessage: String? = null,
)

data class ReviewAnswerUi(
    val questionText: String,
    val explanation: String,
    val options: List<ReviewOptionUi>,
)

data class ReviewOptionUi(
    val optionId: String,
    val text: String,
    val selected: Boolean,
    val correct: Boolean,
)
