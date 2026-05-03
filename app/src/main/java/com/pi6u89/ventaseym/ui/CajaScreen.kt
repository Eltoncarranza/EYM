package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Documentación: Pantalla donde la cajera inicia su turno registrando el efectivo base.
 */
@Composable
fun CajaScreen(
    alAbrirCaja: () -> Unit // Función que avisará al enrutador que debe cambiar de pantalla
) {
    // Variable que guarda lo que se escribe en el campo. Por defecto es 0.0
    var montoInicialTexto by remember { mutableStateOf("0.0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Apertura de Caja",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "¿Con cuánto efectivo inicias tu turno en E&M?")

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto diseñado específicamente para números
        OutlinedTextField(
            value = montoInicialTexto,
            onValueChange = { montoInicialTexto = it },
            label = { Text("Monto Inicial (S/.)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para confirmar y avanzar
        Button(
            onClick = {
                // TODO: Aquí luego añadiremos el código para guardar en la base de datos
                alAbrirCaja()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abrir Turno")
        }
    }
}