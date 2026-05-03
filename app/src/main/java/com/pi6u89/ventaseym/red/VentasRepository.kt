package com.pi6u89.ventaseym.red

import com.pi6u89.ventaseym.modelos.Venta
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Documentación: Esta clase gestiona el envío de ventas a Supabase.
 */
class VentasRepository {
    private val client = ClienteSupabase.cliente

    /**
     * Envía una venta completa a la tabla 'ventas' en Supabase.
     * Se ejecuta en un hilo secundario (IO) para no congelar la pantalla.
     */
    suspend fun registrarVenta(nuevaVenta: Venta): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Insertamos la venta en la tabla 'ventas'
                client.postgrest["ventas"].insert(nuevaVenta)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}