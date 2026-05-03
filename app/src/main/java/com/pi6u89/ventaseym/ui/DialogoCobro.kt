package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.ItemPedido

@Composable
fun DialogoCobro(
    esParaLlevar: Boolean,
    carrito: List<ItemPedido>,
    alConfirmarVenta: (metodoPago: String, nombreCliente: String, esFiado: Boolean, tipoEnvase: String) -> Unit,
    alCancelar: () -> Unit
) {
    val precioTotal = carrito.sumOf { it.precioIngresadoManualmente }
    var metodoPago by remember { mutableStateOf("Efectivo") }
    var nombreCliente by remember { mutableStateOf("") }
    var esFiado by remember { mutableStateOf(false) }
    var tipoEnvase by remember { mutableStateOf("Taper") }

    // Estado para la confirmación de fiado
    var mostrarConfirmacionFiado by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Resumen de Venta") },
        text = {
            Column {
                Text("Total: S/. $precioTotal", style = MaterialTheme.typography.headlineSmall)

                if (esParaLlevar) {
                    Text("Opción de entrega:")
                    val opciones = listOf("Taper", "Trajo su Plato", "Preste Plato")
                    opciones.forEach { opcion ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = tipoEnvase == opcion, onClick = { tipoEnvase = opcion })
                            Text(opcion)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Switch de Fiado con confirmación
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Se Fió?")
                    Switch(
                        checked = esFiado,
                        onCheckedChange = {
                            if (it) mostrarConfirmacionFiado = true else esFiado = false
                        }
                    )
                }

                OutlinedTextField(
                    value = nombreCliente,
                    onValueChange = { nombreCliente = it },
                    label = { Text("Nombre Cliente ${if (esFiado || tipoEnvase == "Preste Plato") "(Obligatorio)*" else ""}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { alConfirmarVenta(metodoPago, nombreCliente, esFiado, tipoEnvase) },
                // Validación: Si es fiado o prestó plato, el nombre es obligatorio
                enabled = !((esFiado || tipoEnvase == "Preste Plato") && nombreCliente.isBlank())
            ) { Text("Cobrar") }
        }
    )

    // Diálogo de confirmación para evitar fiados accidentales
    if (mostrarConfirmacionFiado) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionFiado = false },
            title = { Text("Confirmar Fiado") },
            text = { Text("¿Está seguro de registrar esta venta como fiada? No sumará al efectivo de hoy.") },
            confirmButton = {
                TextButton(onClick = {
                    esFiado = true
                    mostrarConfirmacionFiado = false
                }) { Text("SÍ, ES FIADO") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionFiado = false }) { Text("CANCELAR") }
            }
        )
    }
}