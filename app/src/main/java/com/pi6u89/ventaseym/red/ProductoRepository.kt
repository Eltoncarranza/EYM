package com.pi6u89.ventaseym.red

import com.pi6u89.ventaseym.modelos.Producto
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Documentación: Esta clase se encarga de traer la lista de platos
 * y bebidas desde la tabla 'productos' de Supabase.
 */
class ProductoRepository {
    private val client = ClienteSupabase.cliente

    suspend fun obtenerProductos(): List<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                // Trae todos los registros de la tabla 'productos'
                client.postgrest["productos"].select().decodeList<Producto>()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Si hay error, devuelve una lista vacía
            }
        }
    }
}