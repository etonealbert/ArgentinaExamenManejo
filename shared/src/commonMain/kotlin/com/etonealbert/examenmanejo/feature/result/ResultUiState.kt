package com.etonealbert.examenmanejo.feature.result

import com.etonealbert.examenmanejo.domain.model.ExamResult

data class ResultUiState(
    val examSessionId: Long,
    val isLoading: Boolean = true,
    val result: ExamResult? = null,
    val errorMessage: String? = null,
)
