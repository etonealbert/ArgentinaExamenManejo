package com.etonealbert.examenmanejo.feature.home

import com.etonealbert.examenmanejo.domain.model.LicenseClass

data class HomeUiState(
    val isLoading: Boolean = true,
    val licenseClasses: List<LicenseClass> = emptyList(),
    val errorMessage: String? = null,
)
