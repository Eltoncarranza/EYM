package com.pi6u89.ventaseym.red

import com.pi6u89.ventaseym.modelos.SesionCaja
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CajaRepository {
    private val client = ClienteSupabase.cliente

    /**
     * Crea un nuevo registro de apertura de caja en Supabase.
     */
    suspend fun abrirCaja(nuevaSesion: SesionCaja): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["sesiones_caja"].insert(nuevaSesion)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}