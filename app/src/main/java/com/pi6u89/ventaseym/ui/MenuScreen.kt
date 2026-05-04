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
import com.pi6u89.ventaseym.red.ProductoRepository
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Documentación: Pantalla de selección de pedidos con carga dinámica desde Supabase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: VentasViewModel,
    numeroMesa: Int?,
    alFinalizarVenta: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Repositorios
    val ventasRepository = remember { VentasRepository() }
    val productoRepository = remember { ProductoRepository() }

    // Estados de datos
    var listaComidas by remember { mutableStateOf(listOf<Producto>()) }
    var listaCafeteria by remember { mutableStateOf(listOf<Producto>()) }
    var estaCargando by remember { mutableStateOf(true) }

    // Estados de UI
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }
    val carritoTemporal = remember { mutableStateListOf<ItemPedido>() }

    // Estados para Diálogos
    var mostrarDialogoPrecio by remember { mutableStateOf(false) }
    var mostrarDialogoCobro by remember { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var precioIngresado by remember { mutableStateOf("") }

    // CARGA DINÁMICA: Se ejecuta al entrar a la pantalla
    LaunchedEffect(Unit) {
        val productosDesdeNube = productoRepository.obtenerProductos()
        // Filtramos por las categorías definidas en tu base de datos
        listaComidas = productosDesdeNube.filter { it.categoria == "Comida" }
        listaCafeteria = productosDesdeNube.filter { it.categoria == "Cafeteria" }
        estaCargando = false
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (numeroMesa != null) "Mesa $numeroMesa" else "Para Llevar") }) },
        bottomBar = {
            Button(
                onClick = { mostrarDialogoCobro = true },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                // Solo se activa si hay productos en el carrito y una sesión de caja abierta
                enabled = carritoTemporal.isNotEmpty() && viewModel.sesionCajaActivaId != null
            ) {
                Text("Cobrar (S/. ${carritoTemporal.sumOf { it.precioIngresadoManualmente }})")
            }
        }
    ) { padding ->
        if (estaCargando) {
            // Indicador visual mientras descargan los datos
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = pestanaSeleccionada) {
                    Tab(selected = pestanaSeleccionada == 0, onClick = { pestanaSeleccionada = 0 }, text = { Text("Comida") })
                    Tab(selected = pestanaSeleccionada == 1, onClick = { pestanaSeleccionada = 1 }, text = { Text("Café") })
                }

                val productosAMostrar = if (pestanaSeleccionada == 0) listaComidas else listaCafeteria

                if (productosAMostrar.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay productos en esta categoría")
                    }
                } else {
                    LazyColumn {
                        items(productosAMostrar) { producto ->
                            TarjetaProducto(producto = producto) {
                                productoSeleccionado = producto
                                precioIngresado = ""
                                mostrarDialogoPrecio = true
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Lógica de Diálogos ---

    // 1. Diálogo para ingresar precio manual
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

    // 2. Diálogo final de cobro
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
                    } else {
                        Toast.makeText(context, "Error al guardar venta", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

/**
 * Componente visual para cada producto en la lista.
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