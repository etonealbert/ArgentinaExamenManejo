package com.etonealbert.examenmanejo.feature.exam

import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot

data class ExamUiState(
    val licenseClassId: String,
    val isLoading: Boolean = true,
    val isFinishing: Boolean = false,
    val sessionId: Long? = null,
    val questions: List<ExamQuestionSnapshot> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
) {
    val currentQuestion: ExamQuestionSnapshot? = questions.getOrNull(currentIndex)
}
