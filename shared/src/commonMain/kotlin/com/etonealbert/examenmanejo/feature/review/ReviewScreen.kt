package com.etonealbert.examenmanejo.feature.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etonealbert.examenmanejo.core.design.AnswerOptionCard

@Composable
fun ReviewScreen(
    state: ReviewUiState,
    onEvent: (ReviewUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Revision", style = MaterialTheme.typography.headlineMedium)
        if (state.isLoading) {
            Text("Cargando respuestas guardadas...")
        } else if (state.answers.isEmpty()) {
            Text("No hay respuestas para revisar.")
        } else {
            state.answers.forEachIndexed { index, answer ->
                Text("Pregunta ${index + 1}", style = MaterialTheme.typography.titleMedium)
                Text(answer.questionText)
                answer.options.forEach { option ->
                    AnswerOptionCard(
                        text = option.text,
                        selected = option.selected,
                        enabled = false,
                        onClick = {},
                        correct = option.correct,
                        feedback = when {
                            option.correct -> "Correcta"
                            option.selected -> "Seleccionada"
                            else -> null
                        },
                    )
                }
                Text(answer.explanation)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { onEvent(ReviewUiEvent.HistoryClicked) }, modifier = Modifier.weight(1f)) {
                Text("Historial")
            }
            OutlinedButton(onClick = { onEvent(ReviewUiEvent.HomeClicked) }, modifier = Modifier.weight(1f)) {
                Text("Inicio")
            }
        }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
