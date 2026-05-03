package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Documentación: Interfaz para capturar el motivo y monto de una salida de dinero.
 */
@Composable
fun DialogoEgreso(
    alConfirmar: (descripcion: String, monto: Double) -> Unit,
    alCancelar: () -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Registrar Egreso (Salida de Dinero)") },
        text = {
            Column {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (ej. Compra de azúcar)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = montoTexto,
                    onValueChange = { montoTexto = it },
                    label = { Text("Monto (S/.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val monto = montoTexto.toDoubleOrNull() ?: 0.0
                    if (descripcion.isNotBlank() && monto > 0) {
                        alConfirmar(descripcion, monto)
                    }
                }
            ) { Text("Registrar Gasto") }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text("Cancelar") }
        }
    )
}