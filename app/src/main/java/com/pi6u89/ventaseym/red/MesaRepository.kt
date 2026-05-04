package com.pi6u89.ventaseym.red

import com.pi6u89.ventaseym.modelos.Mesa
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MesaRepository {
    private val client = ClienteSupabase.cliente

    suspend fun obtenerMesas(): List<Mesa> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["mesas"].select().decodeList<Mesa>().sortedBy { it.numero }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}