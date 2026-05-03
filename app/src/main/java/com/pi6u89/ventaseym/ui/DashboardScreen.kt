package com.pi6u89.ventaseym.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.Egreso
import com.pi6u89.ventaseym.red.EgresoRepository
import kotlinx.coroutines.launch

/**
 * Documentación: Pantalla principal que gestiona el estado de las mesas y los egresos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: VentasViewModel,
    alSeleccionarMesa: (Int) -> Unit,
    alSeleccionarParaLlevar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val egresoRepo = remember { EgresoRepository() }
    var mostrarDialogoEgreso by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Mesas - E&M") },
                actions = {
                    TextButton(onClick = { mostrarDialogoEgreso = true }) {
                        Text("Egresos", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Button(
                onClick = { alSeleccionarParaLlevar() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Venta PARA LLEVAR", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Estado de Mesas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Cuadrícula de Mesas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(4) { index ->
                    val numeroMesa = index + 1
                    // Aquí llamamos a la función definida abajo
                    TarjetaMesa(
                        numeroMesa = numeroMesa,
                        estaOcupada = false,
                        alHacerClic = { alSeleccionarMesa(numeroMesa) }
                    )
                }
            }
        }
    }

    // Lógica del Diálogo de Egreso
    if (mostrarDialogoEgreso) {
        DialogoEgreso(
            alCancelar = { mostrarDialogoEgreso = false },
            // CORRECCIÓN: Tipos explícitos para evitar "Cannot infer type"
            alConfirmar = { descripcion: String, monto: Double ->
                val nuevoEgreso = Egreso(
                    sesionCajaId = viewModel.sesionCajaActivaId ?: "",
                    descripcion = descripcion,
                    monto = monto
                )
                scope.launch {
                    val exito = egresoRepo.registrarEgreso(nuevoEgreso)
                    if (exito) {
                        Toast.makeText(context, "Gasto registrado", Toast.LENGTH_SHORT).show()
                        mostrarDialogoEgreso = false
                    } else {
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

/**
 * Documentación: Componente visual para la entrada de gastos.
 */
@Composable
fun DialogoEgreso(
    alConfirmar: (String, Double) -> Unit,
    alCancelar: () -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Registrar Gasto") },
        text = {
            Column {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Motivo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = montoTexto,
                    onValueChange = { montoTexto = it },
                    label = { Text("Monto S/.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val m = montoTexto.toDoubleOrNull() ?: 0.0
                if (descripcion.isNotBlank() && m > 0) alConfirmar(descripcion, m)
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text("Cancelar") }
        }
    )
}

/**
 * Documentación: Componente visual para cada mesa.
 */
@Composable
fun TarjetaMesa(numeroMesa: Int, estaOcupada: Boolean, alHacerClic: () -> Unit) {
    val colorFondo = if (estaOcupada) Color(0xFFE57373) else Color(0xFF81C784)

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
            Text(
                text = if (estaOcupada) "Ocupada" else "Libre",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}