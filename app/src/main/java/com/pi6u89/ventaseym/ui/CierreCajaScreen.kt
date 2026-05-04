package com.pi6u89.ventaseym.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.red.CajaRepository
import kotlinx.coroutines.launch

@Composable
fun CierreCajaScreen(
    viewModel: VentasViewModel,
    montoInicial: Double,
    ventasEfectivo: Double,
    egresos: Double,
    alFinalizarCierre: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { CajaRepository() }

    var montoFisicoTexto by remember { mutableStateOf("") }

    // Cálculo del dinero que debería haber en caja
    val totalEsperado = montoInicial + ventasEfectivo - egresos
    val montoFisico = montoFisicoTexto.toDoubleOrNull() ?: 0.0
    val diferencia = montoFisico - totalEsperado

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Resumen de Cierre", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        // Tabla de resumen
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Monto Inicial: S/. $montoInicial")
                Text("Ventas Efectivo: + S/. $ventasEfectivo")
                Text("Egresos: - S/. $egresos", color = MaterialTheme.colorScheme.error)
                Divider(Modifier.padding(vertical = 8.dp))
                Text("Total Esperado: S/. $totalEsperado", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = montoFisicoTexto,
            onValueChange = { montoFisicoTexto = it },
            label = { Text("Monto físico en caja (S/.)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Mostrar si falta o sobra dinero
        if (montoFisicoTexto.isNotEmpty()) {
            Text(
                text = when {
                    diferencia == 0.0 -> "Caja Cuadrada"
                    diferencia > 0.0 -> "Sobrante: S/. $diferencia"
                    else -> "Faltante: S/. ${kotlin.math.abs(diferencia)}"
                },
                color = if (diferencia >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                scope.launch {
                    val exito = repository.cerrarCaja(
                        idSesion = viewModel.sesionCajaActivaId ?: "",
                        montoFinalReal = montoFisico,
                        ventasEfectivo = ventasEfectivo,
                        totalEgresos = egresos
                    )
                    if (exito) {
                        viewModel.sesionCajaActivaId = null // Limpiamos la sesión global
                        Toast.makeText(context, "Turno cerrado exitosamente", Toast.LENGTH_SHORT).show()
                        alFinalizarCierre()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = montoFisicoTexto.isNotEmpty()
        ) {
            Text("Finalizar Turno")
        }
    }
}