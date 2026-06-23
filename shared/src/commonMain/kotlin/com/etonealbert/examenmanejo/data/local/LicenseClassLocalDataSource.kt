package com.etonealbert.examenmanejo.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.etonealbert.examenmanejo.data.mapper.toDomain
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase
import com.etonealbert.examenmanejo.domain.model.LicenseClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LicenseClassLocalDataSource(
    private val database: ExamenManejoDatabase,
) {
    fun observeLicenseClasses(): Flow<List<LicenseClass>> = database.contentQueries
        .selectAllLicenseClasses()
        .asFlow()
        .mapToList(Dispatchers.Default)
        .map { rows -> rows.map { it.toDomain() } }
}
