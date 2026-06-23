package com.etonealbert.examenmanejo.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = ExamenManejoDatabase.Schema,
        name = "examen_manejo.db",
    )
}
