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
    /**
     * Consulta Supabase para sumar todas las ventas en efectivo de la sesión actual.
     */
    suspend fun obtenerTotalVentasEfectivo(idSesion: String): Double {
        return withContext(Dispatchers.IO) {
            try {
                val ventas = client.postgrest["ventas"].select {
                    filter {
                        eq("sesion_caja_id", idSesion)
                        eq("metodo_pago", "Efectivo")
                        eq("es_fiado", false) // No sumamos lo que no se pagó
                    }
                }.decodeList<Venta>()

                ventas.sumOf { it.precioTotal }
            } catch (e: Exception) {
                e.printStackTrace()
                0.0
            }
        }
    }
}