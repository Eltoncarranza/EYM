package com.pi6u89.ventaseym.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
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
 * Pantalla principal que gestiona el estado de las mesas, gastos y preparación del cierre de caja.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: VentasViewModel,
    alSeleccionarMesa: (Mesa) -> Unit,
    alSeleccionarParaLlevar: () -> Unit,
    alIrACierre: (Double, Double) -> Unit, // Callback para navegar al cierre
    alIrAFiados: () -> Unit,
    alIrAHistorial: () -> Unit

) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inicialización de Repositorios
    val egresoRepo = remember { EgresoRepository() }
    val mesaRepo = remember { MesaRepository() }
    val ventasRepo = remember { VentasRepository() }

    // Estados de la pantalla
    var listaMesas by remember { mutableStateOf(listOf<Mesa>()) }
    var estaCargando by remember { mutableStateOf(true) }
    var mostrarDialogoEgreso by remember { mutableStateOf(false) }

    // Carga las mesas desde Supabase al iniciar[cite: 2]
    LaunchedEffect(Unit) {
        listaMesas = mesaRepo.obtenerMesas()
        estaCargando = false
    }

    /**
     * Calcula los totales antes de navegar al arqueo.[cite: 2]
     */
    fun prepararCierre() {
        scope.launch {
            val idSesion = viewModel.sesionCajaActivaId
            if (idSesion == null) {
                Toast.makeText(context, "No hay una sesión de caja abierta", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val totalVentas = ventasRepo.obtenerTotalVentasEfectivo(idSesion)
                val totalGastos = egresoRepo.obtenerTotalEgresos(idSesion)
                alIrACierre(totalVentas, totalGastos) // Ejecuta la navegación[cite: 2]
            } catch (e: Exception) {
                Toast.makeText(context, "Error al calcular el balance: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Mesas - E&M") },
                actions = {
                    IconButton(onClick = { alIrAHistorial() }) {
                        Icon(
                            imageVector = Icons.Default.History, // Asegúrate de importar Icons.Default.History
                            contentDescription = "Historial"
                        )
                    }
                    // Botón para ver deudas (Fiados)
                    IconButton(onClick = { alIrAFiados() }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Fiados",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Botón para registrar egresos
                    TextButton(onClick = { mostrarDialogoEgreso = true }) {
                        Text("Egresos", color = MaterialTheme.colorScheme.error)
                    }
                    // Botón para iniciar el proceso de cierre
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
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
                        Toast.makeText(context, "Egreso guardado con éxito", Toast.LENGTH_SHORT).show()
                        mostrarDialogoEgreso = false
                    }
                }
            }
        )
    }
}

/**
 * Componente visual de una mesa individual.[cite: 2]
 */
@Composable
fun TarjetaMesa(numeroMesa: Int, estaOcupada: Boolean, alHacerClic: () -> Unit) {
    val colorFondo = if (estaOcupada) Color(0xFFE57373) else Color(0xFF81C784)
    val textoEstado = if (estaOcupada) "Ocupada" else "Libre"

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(color = colorFondo, shape = RoundedCornerShape(16.dp))
            .clickable { alHacerClic() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Mesa $numeroMesa",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = textoEstado, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
    }
}