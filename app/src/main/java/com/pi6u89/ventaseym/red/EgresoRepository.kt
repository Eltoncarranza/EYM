package com.pi6u89.ventaseym.red

import com.pi6u89.ventaseym.modelos.Egreso
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Documentación: Gestiona la inserción de gastos de caja en Supabase.
 */
class EgresoRepository {
    private val client = ClienteSupabase.cliente

    suspend fun registrarEgreso(nuevoEgreso: Egreso): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["egresos"].insert(nuevoEgreso)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    /**
     * Consulta Supabase para sumar todos los egresos de la sesión actual.
     */
    suspend fun obtenerTotalEgresos(idSesion: String): Double {
        return withContext(Dispatchers.IO) {
            try {
                val egresos = client.postgrest["egresos"].select {
                    filter { eq("sesion_caja_id", idSesion) }
                }.decodeList<Egreso>()

                egresos.sumOf { it.monto }
            } catch (e: Exception) {
                e.printStackTrace()
                0.0
            }
        }
    }
}