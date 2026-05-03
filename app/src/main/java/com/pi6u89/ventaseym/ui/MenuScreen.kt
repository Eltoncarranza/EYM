package com.pi6u89.ventaseym.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.Producto
import com.pi6u89.ventaseym.modelos.ItemPedido
import com.pi6u89.ventaseym.modelos.Venta
import com.pi6u89.ventaseym.red.VentasRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: VentasViewModel,
    numeroMesa: Int?,
    alFinalizarVenta: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ventasRepository = remember { VentasRepository() }

    // Estados de la pantalla
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }
    val carritoTemporal = remember { mutableStateListOf<ItemPedido>() }

    // Estados para Diálogos
    var mostrarDialogoPrecio by remember { mutableStateOf(false) }
    var mostrarDialogoCobro by remember { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var precioIngresado by remember { mutableStateOf("") }

    // Listas de productos (Ejemplo)
    val listaComidas = listOf(
        Producto("1", "Tallarín", "Comida"),
        Producto("2", "Salchipollo", "Comida")
    )
    val listaCafeteria = listOf(
        Producto("6", "Americano", "Cafetería"),
        Producto("7", "Capuchino", "Cafetería")
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (numeroMesa != null) "Mesa $numeroMesa" else "Para Llevar") }) },
        bottomBar = {
            Button(
                onClick = { mostrarDialogoCobro = true },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = carritoTemporal.isNotEmpty()
            ) {
                Text("Cobrar (S/. ${carritoTemporal.sumOf { it.precioIngresadoManualmente }})")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(selected = pestanaSeleccionada == 0, onClick = { pestanaSeleccionada = 0 }, text = { Text("Comida") })
                Tab(selected = pestanaSeleccionada == 1, onClick = { pestanaSeleccionada = 1 }, text = { Text("Café") })
            }

            val productos = if (pestanaSeleccionada == 0) listaComidas else listaCafeteria

            LazyColumn {
                items(productos) { producto ->
                    // Aquí se usa la función corregida
                    TarjetaProducto(producto = producto) {
                        productoSeleccionado = producto
                        precioIngresado = ""
                        mostrarDialogoPrecio = true
                    }
                }
            }
        }
    }

    // Lógica de Diálogos
    if (mostrarDialogoPrecio && productoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPrecio = false },
            title = { Text("Precio para ${productoSeleccionado?.nombre}") },
            text = {
                OutlinedTextField(
                    value = precioIngresado,
                    onValueChange = { precioIngresado = it },
                    label = { Text("S/.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    val precio = precioIngresado.toDoubleOrNull() ?: 0.0
                    if (precio > 0) {
                        carritoTemporal.add(ItemPedido(nombrePlato = productoSeleccionado!!.nombre, precioIngresadoManualmente = precio))
                        mostrarDialogoPrecio = false
                    }
                }) { Text("Agregar") }
            }
        )
    }

    if (mostrarDialogoCobro) {
        DialogoCobro(
            esParaLlevar = numeroMesa == null,
            carrito = carritoTemporal,
            alCancelar = { mostrarDialogoCobro = false },
            alConfirmarVenta = { metodoPago, cliente, esFiado, tipoEnvase ->
                val nuevaVenta = Venta(
                    id = UUID.randomUUID().toString(),
                    sesionCajaId = viewModel.sesionCajaActivaId ?: "",
                    mesaId = if (numeroMesa != null) "MESA_$numeroMesa" else null,
                    esParaLlevar = numeroMesa == null,
                    tipoEnvase = tipoEnvase,
                    precioTotal = carritoTemporal.sumOf { it.precioIngresadoManualmente },
                    metodoPago = metodoPago,
                    montoRecibido = 0.0,
                    vuelto = 0.0,
                    nombreCliente = cliente,
                    esFiado = esFiado,
                    saldoPendiente = if (esFiado) carritoTemporal.sumOf { it.precioIngresadoManualmente } else 0.0
                )

                scope.launch {
                    val exito = ventasRepository.registrarVenta(nuevaVenta)
                    if (exito) {
                        Toast.makeText(context, "Venta Exitosa", Toast.LENGTH_SHORT).show()
                        carritoTemporal.clear()
                        mostrarDialogoCobro = false
                        alFinalizarVenta()
                    }
                }
            }
        )
    }
}

/**
 * ESTA ES LA FUNCIÓN QUE FALTABA
 */
@Composable
fun TarjetaProducto(producto: Producto, alHacerClic: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { alHacerClic() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}