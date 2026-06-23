package com.etonealbert.examenmanejo

import androidx.compose.ui.window.ComposeUIViewController
import com.etonealbert.examenmanejo.core.di.initKoin

fun MainViewController() = run {
    initKoin()

    ComposeUIViewController { App() }
}
