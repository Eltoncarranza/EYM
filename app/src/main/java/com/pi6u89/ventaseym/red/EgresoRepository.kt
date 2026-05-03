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
}