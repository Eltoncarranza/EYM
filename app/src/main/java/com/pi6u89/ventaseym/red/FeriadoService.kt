package com.pi6u89.ventaseym.red

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

/**
 * Servicio para identificar feriados nacionales en Perú.
 */
object FeriadoService {
    @RequiresApi(Build.VERSION_CODES.O)
    private val feriados2026 = listOf(
        LocalDate.of(2026, 1, 1),   // Año Nuevo
        LocalDate.of(2026, 4, 2),   // Jueves Santo
        LocalDate.of(2026, 4, 3),   // Viernes Santo
        LocalDate.of(2026, 5, 1),   // Día del Trabajo
        LocalDate.of(2026, 6, 29),  // San Pedro y San Pablo
        LocalDate.of(2026, 7, 28),  // Fiestas Patrias
        LocalDate.of(2026, 7, 29),  // Fiestas Patrias
        LocalDate.of(2026, 8, 30),  // Santa Rosa de Lima
        LocalDate.of(2026, 10, 8),  // Combate de Angamos
        LocalDate.of(2026, 11, 1),  // Todos los Santos
        LocalDate.of(2026, 12, 8),  // Inmaculada Concepción
        LocalDate.of(2026, 12, 25)  // Navidad
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun esFeriado(fecha: LocalDate): Boolean {
        return feriados2026.contains(fecha)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerNombreFeriado(fecha: LocalDate): String? {
        return if (esFeriado(fecha)) "Feriado Calendario" else null
    }
}