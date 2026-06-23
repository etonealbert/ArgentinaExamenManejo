package com.etonealbert.examenmanejo.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Datos locales", style = MaterialTheme.typography.titleMedium)
                Text(state.localDataDescription)
                Text("No hay backend, cuenta, sincronizacion en la nube, analitica ni notificaciones push en este MVP.")
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Contenido demo", style = MaterialTheme.typography.titleMedium)
                Text("Las preguntas incluidas son DEMO_UNVERIFIED. Sirven para probar el flujo y no deben tratarse como contenido oficial.")
                Text("Esta app no esta afiliada, aprobada ni patrocinada por ninguna autoridad de transito o entidad gubernamental.")
            }
        }
        OutlinedButton(onClick = { onEvent(SettingsUiEvent.HomeClicked) }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al inicio")
        }
    }
}
