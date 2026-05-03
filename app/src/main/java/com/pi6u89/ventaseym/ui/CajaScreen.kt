package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.SesionCaja
import com.pi6u89.ventaseym.red.CajaRepository
import kotlinx.coroutines.launch

/**
 * Documentación: Pantalla para iniciar el turno. Registra el monto inicial
 * y guarda el ID de la sesión en el ViewModel.
 */
@Composable
fun CajaScreen(
    viewModel: VentasViewModel,
    idUsuarioFirebase: String,
    alAbrirCaja: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val repository = remember { CajaRepository() }
    var montoInicialTexto by remember { mutableStateOf("0.0") }
    var estaCargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Apertura de Caja", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ingrese el monto inicial en efectivo:")

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = montoInicialTexto,
            onValueChange = { montoInicialTexto = it },
            label = { Text("Monto Inicial (S/.)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                estaCargando = true
                val monto = montoInicialTexto.toDoubleOrNull() ?: 0.0
                val nuevaSesion = SesionCaja(
                    idCajera = idUsuarioFirebase,
                    montoInicialEfectivo = monto
                )

                scope.launch {
                    val exito = repository.abrirCaja(nuevaSesion)
                    if (exito) {
                        viewModel.sesionCajaActivaId = nuevaSesion.id
                        alAbrirCaja()
                    }
                    estaCargando = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaCargando
        ) {
            Text(if (estaCargando) "Abriendo..." else "Abrir Turno")
        }
    }
}