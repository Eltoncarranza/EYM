package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Rutas {
    LOGIN,
    CAJA,
    DASHBOARD // Agregamos la ruta del Dashboard
}

@Composable
fun EnrutadorApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN.name) {

        // 1. RUTA DE LOGIN
        composable(route = Rutas.LOGIN.name) {
            LoginScreen(
                alIniciarSesionExitoso = {
                    navController.navigate(Rutas.CAJA.name) {
                        popUpTo(Rutas.LOGIN.name) { inclusive = true }
                    }
                }
            )
        }

        // 2. RUTA DE APERTURA DE CAJA
        composable(route = Rutas.CAJA.name) {
            // Llamamos a nuestra nueva pantalla real
            CajaScreen(
                alAbrirCaja = {
                    // Al abrir la caja, viajamos al Dashboard Principal
                    navController.navigate(Rutas.DASHBOARD.name) {
                        popUpTo(Rutas.CAJA.name) { inclusive = true }
                    }
                }
            )
        }

        // 3. RUTA DEL DASHBOARD (Temporal para evitar errores al avanzar)
        composable(route = Rutas.DASHBOARD.name) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "¡Bienvenido al Dashboard de E&M! Aquí irán las mesas.")
            }
        }
    }
}