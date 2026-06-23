package com.etonealbert.examenmanejo

import androidx.compose.ui.window.ComposeUIViewController
import com.etonealbert.examenmanejo.core.di.initKoin
import org.koin.core.context.GlobalContext

fun MainViewController() = run {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }

    ComposeUIViewController { App() }
}
