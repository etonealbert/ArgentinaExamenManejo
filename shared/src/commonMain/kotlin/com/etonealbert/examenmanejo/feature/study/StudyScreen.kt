package com.etonealbert.examenmanejo.feature.study

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.etonealbert.examenmanejo.core.design.AnswerOptionCard

@Composable
fun StudyScreen(
    state: StudyUiState,
    onEvent: (StudyUiEvent) -> Unit,
) {
    val question = state.currentQuestion
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Estudio Clase ${state.licenseClassId}", style = MaterialTheme.typography.headlineMedium)
        Text("Modo practica con respuesta inmediata. Contenido demo no oficial.")
        when {
            state.isLoading -> Text("Cargando preguntas locales...")
            question == null -> Text("No hay preguntas demo disponibles.")
            else -> {
                Text("Pregunta ${state.currentIndex + 1} de ${state.questions.size}")
                Text(question.text, style = MaterialTheme.typography.titleMedium)
                question.options.sortedBy { it.position }.forEach { option ->
                    val selected = state.selectedOptionId == option.id
                    val showFeedback = state.selectedOptionId != null
                    AnswerOptionCard(
                        text = option.text,
                        selected = selected,
                        enabled = true,
                        onClick = { onEvent(StudyUiEvent.SelectAnswer(option.id)) },
                        correct = if (showFeedback) option.isCorrect else null,
                        feedback = when {
                            !showFeedback -> null
                            option.isCorrect -> "Respuesta correcta"
                            selected -> "No es la respuesta correcta"
                            else -> null
                        },
                    )
                }
                if (state.selectedOptionId != null) {
                    Text(question.explanation)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { onEvent(StudyUiEvent.HomeClicked) }, modifier = Modifier.weight(1f)) {
                        Text("Inicio")
                    }
                    Button(onClick = { onEvent(StudyUiEvent.NextClicked) }, modifier = Modifier.weight(1f)) {
                        Text("Siguiente")
                    }
                }
            }
        }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
