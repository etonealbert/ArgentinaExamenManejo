package com.etonealbert.examenmanejo.data.local

import com.etonealbert.examenmanejo.db.ExamenManejoDatabase

class DatabaseProvider(driverFactory: DriverFactory) {
    val database: ExamenManejoDatabase by lazy { ExamenManejoDatabase(driverFactory.createDriver()) }
}
