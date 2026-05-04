package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Componente visual para registrar salidas de dinero de la caja.
 * @param alConfirmar Acción que recibe la descripción y el monto del gasto.
 * @param alCancelar Acción para cerrar el diálogo sin guardar.
 */
@Composable
fun DialogoEgreso(
    alConfirmar: (String, Double) -> Unit,
    alCancelar: () -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Registrar Gasto") },
        text = {
            Column {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Motivo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = montoTexto,
                    onValueChange = { montoTexto = it },
                    label = { Text("Monto S/.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val m = montoTexto.toDoubleOrNull() ?: 0.0
                if (descripcion.isNotBlank() && m > 0) alConfirmar(descripcion, m)
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text("Cancelar") }
        }
    )
}