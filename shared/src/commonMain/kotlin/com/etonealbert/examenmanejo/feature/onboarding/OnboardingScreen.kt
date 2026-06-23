package com.etonealbert.examenmanejo.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onEvent: (OnboardingUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Examen de Manejo", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Practica local y sin conexion", style = MaterialTheme.typography.titleMedium)
                Text("El MVP funciona completamente offline. Las preguntas demo se importan al almacenamiento local y SQLDelight queda como fuente de verdad.")
                Text("Contenido demo no oficial: las preguntas estan marcadas como DEMO_UNVERIFIED y no reemplazan material de la autoridad de transito.")
            }
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onEvent(OnboardingUiEvent.CompleteClicked) },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.isSaving) "Guardando..." else "Empezar")
        }
    }
}
