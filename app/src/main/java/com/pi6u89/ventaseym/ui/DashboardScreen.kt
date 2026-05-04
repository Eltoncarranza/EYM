package com.pi6u89.ventaseym.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.Egreso
import com.pi6u89.ventaseym.modelos.Mesa
import com.pi6u89.ventaseym.red.EgresoRepository
import com.pi6u89.ventaseym.red.MesaRepository
import com.pi6u89.ventaseym.red.VentasRepository
import kotlinx.coroutines.launch

/**
 * Documentación: Pantalla principal que gestiona mesas, egresos y preparación de cierre.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: VentasViewModel,
    alSeleccionarMesa: (Mesa) -> Unit,
    alSeleccionarParaLlevar: () -> Unit,
    alIrACierre: (ventas: Double, egresos: Double) -> Unit // Nuevo callback para navegación
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inicialización de Repositorios
    val egresoRepo = remember { EgresoRepository() }
    val mesaRepo = remember { MesaRepository() }
    val ventasRepo = remember { VentasRepository() }

    // Estados de la UI
    var listaMesas by remember { mutableStateOf(listOf<Mesa>()) }
    var estaCargando by remember { mutableStateOf(true) }
    var mostrarDialogoEgreso by remember { mutableStateOf(false) }

    // Carga inicial de datos
    LaunchedEffect(Unit) {
        listaMesas = mesaRepo.obtenerMesas()
        estaCargando = false
    }

    // Lógica de preparación de cierre (ahora dentro del scope correcto)
    fun prepararCierre() {
        scope.launch {
            val idSesion = viewModel.sesionCajaActivaId
            if (idSesion == null) {
                Toast.makeText(context, "No hay sesión activa", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                // Obtenemos los totales reales desde la base de datos
                val totalVentas = ventasRepo.obtenerTotalVentasEfectivo(idSesion)
                val totalGastos = egresoRepo.obtenerTotalEgresos(idSesion)

                // Navegamos pasando la información calculada
                alIrACierre(totalVentas, totalGastos)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al calcular totales", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Mesas - E&M") },
                actions = {
                    // Botón para Registrar Gastos
                    IconButton(onClick = { mostrarDialogoEgreso = true }) {
                        Text("S/.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                    // Botón para Iniciar Arqueo/Cierre
                    TextButton(onClick = { prepararCierre() }) {
                        Text("Cerrar Caja")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (estaCargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                Button(
                    onClick = alSeleccionarParaLlevar,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("VENTA PARA LLEVAR", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Estado de Mesas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listaMesas) { mesa ->
                        TarjetaMesa(
                            numeroMesa = mesa.numero,
                            estaOcupada = mesa.estaOcupada,
                            alHacerClic = { alSeleccionarMesa(mesa) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de Egreso (Reutilizando tu lógica previa)
    if (mostrarDialogoEgreso) {
        DialogoEgreso(
            alCancelar = { mostrarDialogoEgreso = false },
            alConfirmar = { descripcion, monto ->
                val nuevoEgreso = Egreso(
                    sesionCajaId = viewModel.sesionCajaActivaId ?: "",
                    descripcion = descripcion,
                    monto = monto
                )
                scope.launch {
                    val exito = egresoRepo.registrarEgreso(nuevoEgreso)
                    if (exito) {
                        Toast.makeText(context, "Gasto guardado", Toast.LENGTH_SHORT).show()
                        mostrarDialogoEgreso = false
                    }
                }
            }
        )
    }
}