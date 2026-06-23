package com.etonealbert.examenmanejo.data.local.seed

import kotlinx.serialization.Serializable

@Serializable
data class QuestionPackDto(
    val packId: String,
    val version: Int,
    val hash: String,
    val title: String,
    val sources: List<SourceDto>,
    val jurisdictions: List<JurisdictionDto>,
    val licenseClasses: List<LicenseClassDto>,
    val categories: List<CategoryDto>,
    val questions: List<QuestionDto>,
)

@Serializable
data class SourceDto(
    val id: String,
    val title: String,
    val url: String,
    val accessedAt: String,
    val attribution: String,
)

@Serializable
data class JurisdictionDto(
    val id: String,
    val displayName: String,
    val level: String,
)

@Serializable
data class LicenseClassDto(
    val id: String,
    val displayName: String,
    val description: String,
    val isEnabled: Boolean,
)

@Serializable
data class CategoryDto(
    val id: String,
    val displayName: String,
)

@Serializable
data class QuestionDto(
    val id: String,
    val licenseClassIds: List<String>,
    val jurisdictionId: String,
    val categoryId: String,
    val sourceId: String,
    val text: String,
    val explanation: String,
    val contentStatus: String,
    val reviewStatus: String,
    val questionStatus: String,
    val contentHash: String,
    val options: List<OptionDto>,
)

@Serializable
data class OptionDto(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val position: Int,
)
