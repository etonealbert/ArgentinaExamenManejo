package com.etonealbert.examenmanejo.core.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ExamenManejoColorScheme = lightColorScheme(
    primary = Color(0xFF0B5CAD),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7E8FF),
    onPrimaryContainer = Color(0xFF001C3A),
    secondary = Color(0xFF49617A),
    background = Color(0xFFF8FAFD),
    surface = Color.White,
    error = Color(0xFFB3261E),
)

@Composable
fun ExamenManejoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ExamenManejoColorScheme,
        content = content,
    )
}
