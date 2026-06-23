package com.etonealbert.examenmanejo.data.local

import com.etonealbert.examenmanejo.db.ExamenManejoDatabase
import kotlin.time.Clock

const val HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
const val SELECTED_JURISDICTION = "selected_jurisdiction"
const val SEED_IMPORT_VERSION = "seed_import_version"

class SettingsLocalDataSource(
    private val database: ExamenManejoDatabase,
    private val nowEpochMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
    fun getString(key: String): String? = database.progressSettingsQueries
        .selectUserSetting(key)
        .executeAsOneOrNull()
        ?.setting_value

    fun setString(key: String, value: String) {
        database.progressSettingsQueries.upsertUserSetting(
            setting_key = key,
            setting_value = value,
            updated_at_epoch_ms = nowEpochMillis(),
        )
    }
}
