package com.etonealbert.examenmanejo.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onEvent: (HistoryUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Historial", style = MaterialTheme.typography.headlineMedium)
        when {
            state.isLoading -> Text("Cargando sesiones completadas...")
            state.results.isEmpty() -> Text("Aun no hay examenes completados.")
            else -> state.results.forEach { result ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onEvent(HistoryUiEvent.ResultClicked(result.sessionId))
                    },
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Sesion #${result.sessionId} - Clase ${result.licenseClassId}", style = MaterialTheme.typography.titleMedium)
                        Text(if (result.passed) "Aprobado" else "No aprobado")
                        Text("Puntaje ${result.scorePercentage}% (${result.correctCount}/${result.totalQuestions})")
                    }
                }
            }
        }
        OutlinedButton(onClick = { onEvent(HistoryUiEvent.HomeClicked) }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al inicio")
        }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
