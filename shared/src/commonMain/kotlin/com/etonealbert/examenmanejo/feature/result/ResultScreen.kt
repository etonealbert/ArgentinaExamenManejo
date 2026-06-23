package com.etonealbert.examenmanejo.feature.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(
    state: ResultUiState,
    onEvent: (ResultUiEvent) -> Unit,
) {
    val result = state.result
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Resultado", style = MaterialTheme.typography.headlineMedium)
        when {
            state.isLoading -> Text("Calculando resultado local...")
            result == null -> Text("No se encontro el resultado.")
            else -> {
                Text(if (result.passed) "Aprobado" else "No aprobado", style = MaterialTheme.typography.headlineSmall)
                Text("Puntaje: ${result.scorePercentage}%")
                Text("Correctas: ${result.correctCount} de ${result.totalQuestions}")
                Button(onClick = { onEvent(ResultUiEvent.ReviewClicked) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Revisar respuestas")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { onEvent(ResultUiEvent.HistoryClicked) }, modifier = Modifier.weight(1f)) {
                        Text("Historial")
                    }
                    OutlinedButton(onClick = { onEvent(ResultUiEvent.HomeClicked) }, modifier = Modifier.weight(1f)) {
                        Text("Inicio")
                    }
                }
            }
        }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
