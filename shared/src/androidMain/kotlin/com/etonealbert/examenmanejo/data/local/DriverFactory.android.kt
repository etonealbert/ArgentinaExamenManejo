package com.etonealbert.examenmanejo.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = ExamenManejoDatabase.Schema,
        context = context,
        name = "examen_manejo.db",
    )
}
