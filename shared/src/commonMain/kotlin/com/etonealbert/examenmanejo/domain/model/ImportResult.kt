package com.etonealbert.examenmanejo.domain.model

sealed interface ImportResult {
    data object AlreadyImported : ImportResult
    data class Imported(val questionCount: Int) : ImportResult
    data class Failed(val reason: String) : ImportResult
}
