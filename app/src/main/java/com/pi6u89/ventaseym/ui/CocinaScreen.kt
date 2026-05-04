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
import com.pi6u89.ventaseym.modelos.ItemPedido
import com.pi6u89.ventaseym.red.VentasRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocinaScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { VentasRepository() }

    // Lista de platos que se mostrarán en pantalla
    var itemsPendientes by remember { mutableStateOf(listOf<ItemPedido>()) }
    var estaCargando by remember { mutableStateOf(true) }

    // Función interna para recargar la lista desde la nube
    fun cargarPedidos() {
        scope.launch {
            estaCargando = true
            itemsPendientes = repo.obtenerPedidosCocina()
            estaCargando = false
        }
    }

    // Carga inicial al abrir la pantalla
    LaunchedEffect(Unit) {
        cargarPedidos()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pedidos Pendientes - Cocina") })
        }
    ) { padding ->
        if (estaCargando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(itemsPendientes) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nombrePlato, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Estado actual: ${item.estado}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        val exito = repo.actualizarEstadoItem(item.idUnicoItem, "Listo")
                                        if (exito) {
                                            Toast.makeText(context, "Plato terminado", Toast.LENGTH_SHORT).show()
                                            cargarPedidos() // Refresca la lista
                                        }
                                    }
                                }
                            ) {
                                Text("Listo")
                            }
                        }
                    }
                }
            }
        }
    }
}