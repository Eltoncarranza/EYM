package com.pi6u89.ventaseym.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.modelos.Egreso
import com.pi6u89.ventaseym.modelos.Venta
import com.pi6u89.ventaseym.red.FeriadoService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReportesScreen(ventas: List<Venta>, egresos: List<Egreso>) {
    var pestañaSeleccionada by remember { mutableIntStateOf(0) }
    val titulos = listOf("Día", "Semana", "Mes")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pestañaSeleccionada) {
            titulos.forEachIndexed { index, titulo ->
                Tab(
                    selected = pestañaSeleccionada == index,
                    onClick = { pestañaSeleccionada = index },
                    text = { Text(titulo) }
                )
            }
        }

        when (pestañaSeleccionada) {
            0 -> ReporteDetalle(ventas, egresos, "Diario")
            1 -> ReporteDetalle(ventas, egresos, "Semanal")
            2 -> ReporteDetalle(ventas, egresos, "Mensual")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReporteDetalle(ventas: List<Venta>, egresos: List<Egreso>, tipo: String) {
    val hoy = LocalDate.now()
    val nombreFeriado = FeriadoService.obtenerNombreFeriado(hoy)

    val totalVentas = ventas.sumOf { it.precioTotal }
    val totalEgresos = egresos.sumOf { it.monto }
    val balance = totalVentas - totalEgresos

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Resumen $tipo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Indicador de Feriado
        if (nombreFeriado != null) {
            Surface(
                color = Color(0xFFFFEB3B),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = " Hoy es: $nombreFeriado ",
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ReporteFila("Total Ingresos:", "S/. $totalVentas", Color(0xFF4CAF50))
                ReporteFila("Total Gastos:", "S/. $totalEgresos", Color(0xFFF44336))
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ReporteFila("Ganancia Neta:", "S/. $balance", MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ReporteFila(etiqueta: String, valor: String, colorValor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta)
        Text(valor, color = colorValor, fontWeight = FontWeight.Bold)
    }
}