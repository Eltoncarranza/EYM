package com.pi6u89.ventaseym.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * Documentación: Guarda el estado global de la aplicación,
 * específicamente el ID de la sesión de caja activa.
 */
class VentasViewModel : ViewModel() {
    var sesionCajaActivaId by mutableStateOf<String?>(null)
}