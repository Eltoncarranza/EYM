package com.pi6u89.ventaseym.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.Venta
import com.pi6u89.ventaseym.red.VentasRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiadosScreen(alVolver: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { VentasRepository() }

    var listaFiados by remember { mutableStateOf(listOf<Venta>()) }
    var estaCargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        listaFiados = repo.obtenerVentasFiadas()
        estaCargando = false
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cuentas por Cobrar") }) }
    ) { padding ->
        if (estaCargando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(listaFiados) { venta ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(venta.nombreCliente ?: "Cliente General", fontWeight = FontWeight.Bold)
                                Text("Deuda: S/. ${venta.saldoPendiente}")
                            }
                            Button(onClick = {
                                scope.launch {
                                    // Lógica simple: Paga el total de la deuda
                                    val exito = repo.registrarPagoFiado(venta.id, venta.saldoPendiente, 0.0)
                                    if (exito) {
                                        Toast.makeText(context, "Pago registrado", Toast.LENGTH_SHORT).show()
                                        listaFiados = repo.obtenerVentasFiadas() // Recargar lista
                                    }
                                }
                            }) {
                                Text("Cobrar Todo")
                            }
                        }
                    }
                }
            }
        }
    }
}