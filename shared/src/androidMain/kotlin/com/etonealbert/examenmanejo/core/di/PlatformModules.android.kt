package com.etonealbert.examenmanejo.core.di

import android.content.Context
import com.etonealbert.examenmanejo.data.local.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DriverFactory(get<Context>()) }
}
