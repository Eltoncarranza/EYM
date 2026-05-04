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
import kotlin.math.abs

/**
 * Pantalla de Arqueo de Caja.
 * Permite comparar el saldo del sistema con el efectivo físico.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CierreCajaScreen(
    viewModel: VentasViewModel,
    ventasEfectivo: Double,
    totalEgresos: Double,
    alFinalizarCierre: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cajaRepo = remember { CajaRepository() }

    // Estado para el ingreso de dinero físico
    var montoFisicoTexto by remember { mutableStateOf("") }

    // Suponemos que el monto inicial está guardado o se recupera de la sesión
    val montoInicial = 0.0 // TODO: Podrías pasarlo como parámetro o recuperarlo del ViewModel
    val totalEsperado = montoInicial + ventasEfectivo - totalEgresos
    val montoFisico = montoFisicoTexto.toDoubleOrNull() ?: 0.0
    val diferencia = montoFisico - totalEsperado

    Scaffold(
        topBar = { TopAppBar(title = { Text("Arqueo de Caja") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp)) {
            Text("Resumen del Turno", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            // Tarjeta de resumen matemático
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Ventas en Efectivo: S/. $ventasEfectivo")
                    Text("Gastos/Egresos: - S/. $totalEgresos", color = MaterialTheme.colorScheme.error)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Debería haber: S/. $totalEsperado", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = montoFisicoTexto,
                onValueChange = { montoFisicoTexto = it },
                label = { Text("Efectivo real en caja (S/.)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (montoFisicoTexto.isNotEmpty()) {
                val colorTexto = if (diferencia >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                Text(
                    text = if (diferencia >= 0) "Sobrante: S/. $diferencia" else "Faltante: S/. ${abs(diferencia)}",
                    color = colorTexto,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        // Llamada al repositorio para cerrar la sesión en Supabase
                        val exito = cajaRepo.cerrarCaja(
                            idSesion = viewModel.sesionCajaActivaId ?: "",
                            montoFinalReal = montoFisico,
                            ventasEfectivo = ventasEfectivo,
                            totalEgresos = totalEgresos
                        )
                        if (exito) {
                            viewModel.sesionCajaActivaId = null // Limpiamos la sesión activa
                            Toast.makeText(context, "Caja cerrada y guardada", Toast.LENGTH_SHORT).show()
                            alFinalizarCierre()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = montoFisicoTexto.isNotEmpty()
            ) {
                Text("Finalizar y Cerrar Turno")
            }
        }
    }
}