package com.etonealbert.examenmanejo.feature.exam

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
fun ExamScreen(
    state: ExamUiState,
    onEvent: (ExamUiEvent) -> Unit,
) {
    val question = state.currentQuestion
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Examen Clase ${state.licenseClassId}", style = MaterialTheme.typography.headlineMedium)
        Text("Sesion local. Tus respuestas se guardan en el dispositivo.")
        when {
            state.isLoading -> Text("Preparando examen demo...")
            question == null -> Text("No hay preguntas para este examen.")
            else -> {
                Text("Pregunta ${state.currentIndex + 1} de ${state.questions.size}")
                Text(question.questionText, style = MaterialTheme.typography.titleMedium)
                question.options.sortedBy { it.position }.forEach { option ->
                    AnswerOptionCard(
                        text = option.text,
                        selected = state.selectedAnswers[question.questionId] == option.optionId,
                        enabled = !state.isFinishing,
                        onClick = { onEvent(ExamUiEvent.SelectAnswer(question.questionId, option.optionId)) },
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { onEvent(ExamUiEvent.PreviousClicked) }, modifier = Modifier.weight(1f)) {
                        Text("Anterior")
                    }
                    OutlinedButton(onClick = { onEvent(ExamUiEvent.NextClicked) }, modifier = Modifier.weight(1f)) {
                        Text("Siguiente")
                    }
                }
                Button(
                    onClick = { onEvent(ExamUiEvent.FinishClicked) },
                    enabled = !state.isFinishing,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (state.isFinishing) "Finalizando..." else "Finalizar examen")
                }
            }
        }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
