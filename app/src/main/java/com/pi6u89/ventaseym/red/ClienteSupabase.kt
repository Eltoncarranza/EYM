package com.pi6u89.ventaseym.red

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Documentación: Este objeto Singleton mantiene una única conexión abierta
 * con nuestra base de datos en Supabase para toda la aplicación.
 */
object ClienteSupabase {

    // REEMPLAZA ESTO CON TU URL DE SUPABASE
    private const val SUPABASE_URL = "https://tu-codigo-aqui.supabase.co"

    // REEMPLAZA ESTO CON TU LLAVE ANON PUBLIC DE SUPABASE
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdxbmxuZ2xtZ2V1bXBsYnFjYmJvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc3NzkxODUsImV4cCI6MjA5MzM1NTE4NX0.2zXUK9beJMA52YDQaoRU2k8HrMfTLWMXPSEJR7SSBnk"

    // Inicializamos el cliente de Supabase instalando el módulo Postgrest (para leer y escribir en tablas)
    val cliente = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
    }
}