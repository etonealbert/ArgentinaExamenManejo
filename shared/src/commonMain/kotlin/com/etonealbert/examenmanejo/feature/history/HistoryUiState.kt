package com.etonealbert.examenmanejo.feature.history

import com.etonealbert.examenmanejo.domain.model.ExamResult

data class HistoryUiState(
    val isLoading: Boolean = true,
    val results: List<ExamResult> = emptyList(),
    val errorMessage: String? = null,
)
