package com.etonealbert.examenmanejo.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
) {
    val classB = state.licenseClasses.firstOrNull { it.id == "B" }
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Preparacion Clase B", style = MaterialTheme.typography.headlineMedium)
        Text("Practica offline con contenido demo local.")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(classB?.displayName ?: "Clase B", style = MaterialTheme.typography.titleLarge)
                Text(classB?.description ?: "Banco demo local para practicar el flujo del examen.")
                Button(
                    onClick = { onEvent(HomeUiEvent.StartExamClicked("B")) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Iniciar examen demo") }
                OutlinedButton(
                    onClick = { onEvent(HomeUiEvent.StudyClicked("B")) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Estudiar preguntas") }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { onEvent(HomeUiEvent.HistoryClicked) }, modifier = Modifier.weight(1f)) {
                Text("Historial")
            }
            OutlinedButton(onClick = { onEvent(HomeUiEvent.SettingsClicked) }, modifier = Modifier.weight(1f)) {
                Text("Ajustes")
            }
        }
        if (state.isLoading) Text("Cargando contenido local...")
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
