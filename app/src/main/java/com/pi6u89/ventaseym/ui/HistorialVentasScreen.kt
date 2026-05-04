package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.Venta
import com.pi6u89.ventaseym.red.VentasRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialVentasScreen(alVolver: () -> Unit) {
    val repo = remember { VentasRepository() }
    var listaVentas by remember { mutableStateOf(listOf<Venta>()) }
    var estaCargando by remember { mutableStateOf(true) }

    // Carga los datos al entrar a la pantalla
    LaunchedEffect(Unit) {
        listaVentas = repo.obtenerHistorialVentas()
        estaCargando = false
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial de Ventas") }) }
    ) { padding ->
        if (estaCargando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(listaVentas) { venta ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (venta.esFiado) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Mesa: ${venta.mesaId ?: "Llevar"}", fontWeight = FontWeight.Bold)
                                Text("S/. ${venta.precioTotal}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                            }
                            Text("Cliente: ${venta.nombreCliente ?: "General"}")
                            Text("Pago: ${venta.metodoPago}")
                            if (venta.esFiado) {
                                Text("DEUDA PENDIENTE", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}