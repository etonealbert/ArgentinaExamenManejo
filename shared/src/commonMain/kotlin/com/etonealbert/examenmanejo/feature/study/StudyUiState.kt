package com.etonealbert.examenmanejo.feature.study

import com.etonealbert.examenmanejo.domain.model.Question

data class StudyUiState(
    val licenseClassId: String,
    val isLoading: Boolean = true,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionId: String? = null,
    val errorMessage: String? = null,
) {
    val currentQuestion: Question? = questions.getOrNull(currentIndex)
}
