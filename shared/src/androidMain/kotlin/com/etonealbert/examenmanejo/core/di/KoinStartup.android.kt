package com.etonealbert.examenmanejo.core.di

import android.content.Context
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

fun initKoin(context: Context) {
    if (GlobalContext.getOrNull() == null) {
        initKoin {
            modules(module { single<Context> { context.applicationContext } })
        }
    }
}
