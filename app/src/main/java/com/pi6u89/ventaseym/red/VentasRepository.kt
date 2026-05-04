package com.pi6u89.ventaseym.red

import com.pi6u89.ventaseym.modelos.ItemPedido
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

    /**
     * Registra un pago a una deuda existente.
     */
    suspend fun registrarPagoFiado(idVenta: String, montoPagado: Double, nuevoSaldo: Double): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["ventas"].update({
                    set("saldo_pendiente", nuevoSaldo)
                    if (nuevoSaldo <= 0) set("es_fiado", false)
                }) {
                    filter { eq("id", idVenta) }
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    /**
     * Obtiene las ventas que tienen deudas activas.
     */
    suspend fun obtenerVentasFiadas(): List<Venta> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["ventas"].select {
                    filter {
                        eq("es_fiado", true)
                        gt("saldo_pendiente", 0)
                    }
                }.decodeList<Venta>()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    /**
     * Registra un abono o pago total a una deuda.
     */
    suspend fun actualizarSaldoFiado(idVenta: String, nuevoSaldo: Double): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["ventas"].update({
                    set("saldo_pendiente", nuevoSaldo)
                    // Si la deuda llega a 0, ya no se considera fiado activo
                    if (nuevoSaldo <= 0) set("es_fiado", false)
                }) {
                    filter { eq("id", idVenta) }
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    /**
     * Recupera todas las ventas registradas en Supabase, ordenadas por la más reciente.
     */
    suspend fun obtenerHistorialVentas(): List<Venta> {
        return withContext(Dispatchers.IO) {
            try {
                // Seleccionamos todas las ventas y las ordenamos por fecha de creación descendente
                client.postgrest["ventas"]
                    .select()
                    .decodeList<Venta>()
                    .sortedByDescending { it.id } // Usamos el ID o un campo de fecha si lo tienes
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
    /**
     * Obtiene todos los platos que están pendientes de ser cocinados.
     */
    suspend fun obtenerPedidosCocina(): List<ItemPedido> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["items_pedidos"].select {
                    filter {
                        neq("estado", "Listo") // Solo traemos lo que no está terminado
                    }
                }.decodeList<ItemPedido>()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
    /**
     * Actualiza el estado de un plato específico en la base de datos.
     * @param idItem El identificador único del plato (idUnicoItem).
     * @param nuevoEstado El nuevo valor (ej: "Listo" o "Preparando").
     */
    suspend fun actualizarEstadoItem(idItem: String, nuevoEstado: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["items_pedidos"].update({
                    set("estado", nuevoEstado)
                }) {
                    filter { eq("idUnicoItem", idItem) }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    /**
     * Consulta las ventas totales entre dos fechas específicas.
     */
    suspend fun obtenerIngresosPorRango(fechaInicio: String, fechaFin: String): Double {
        return withContext(Dispatchers.IO) {
            try {
                val ventas = client.postgrest["ventas"].select {
                    filter {
                        gte("creado_en", fechaInicio)
                        lte("creado_en", fechaFin)
                    }
                }.decodeList<Venta>()
                ventas.sumOf { it.precioTotal }
            } catch (e: Exception) {
                0.0
            }
        }
    }
}