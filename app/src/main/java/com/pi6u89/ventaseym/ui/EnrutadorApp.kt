package com.pi6u89.ventaseym.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * Enumeración para centralizar los nombres de las rutas de la app.
 * Se añade FIADOS para gestionar las deudas de los clientes.
 */
enum class Rutas { LOGIN, CAJA, DASHBOARD, MENU, CIERRE, FIADOS, HISTORIAL }

@Composable
fun EnrutadorApp() {
    val navController = rememberNavController()
    // Única instancia del ViewModel compartida en toda la navegación
    val ventasViewModel: VentasViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN.name

    ) {

        // 1. Pantalla de Inicio de Sesión
        composable(Rutas.LOGIN.name) {
            LoginScreen(
                alIniciarSesionExitoso = { idUsuarioFirebase ->
                    navController.navigate(Rutas.CAJA.name)
                }
            )
        }

        // 2. Pantalla de Apertura de Turno
        composable(Rutas.CAJA.name) {
            CajaScreen(
                viewModel = ventasViewModel,
                idUsuarioFirebase = "ID_PRUEBA_123", // Reemplazar con ID real de Firebase
                alAbrirCaja = {
                    navController.navigate(Rutas.DASHBOARD.name) {
                        popUpTo(Rutas.CAJA.name) { inclusive = true }
                    }
                }
            )
        }

        // 3. Panel Principal (Dashboard)
// Dentro de EnrutadorApp.kt
        composable(Rutas.DASHBOARD.name) {
            DashboardScreen(
                viewModel = ventasViewModel,
                alSeleccionarMesa = { mesa ->
                    navController.navigate("${Rutas.MENU.name}/${mesa.numero}")
                },
                alSeleccionarParaLlevar = {
                    navController.navigate("${Rutas.MENU.name}/0")
                },
                alIrACierre = { ventas, egresos ->
                    navController.navigate("${Rutas.CIERRE.name}/$ventas/$egresos")
                },
                alIrAFiados = {
                    navController.navigate(Rutas.FIADOS.name)
                },
                // AQUÍ AGREGAS LA LÍNEA QUE ME PASASTE:
                alIrAHistorial = {
                    navController.navigate(Rutas.HISTORIAL.name)
                }
            )
        }

        // 4. Pantalla de Selección de Menú
        composable(
            route = "${Rutas.MENU.name}/{mesaId}",
            arguments = listOf(navArgument("mesaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val mesaId = backStackEntry.arguments?.getInt("mesaId") ?: 0
            MenuScreen(
                viewModel = ventasViewModel,
                numeroMesa = if (mesaId > 0) mesaId else null,
                alFinalizarVenta = {
                    navController.popBackStack()
                }
            )
        }

        // 5. Pantalla de Arqueo / Cierre de Caja
        composable(
            route = "${Rutas.CIERRE.name}/{ventas}/{egresos}",
            arguments = listOf(
                navArgument("ventas") { type = NavType.StringType },
                navArgument("egresos") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ventas = backStackEntry.arguments?.getString("ventas")?.toDoubleOrNull() ?: 0.0
            val egresos = backStackEntry.arguments?.getString("egresos")?.toDoubleOrNull() ?: 0.0

            CierreCajaScreen(
                viewModel = ventasViewModel,
                ventasEfectivo = ventas,
                totalEgresos = egresos,
                alFinalizarCierre = {
                    navController.navigate(Rutas.LOGIN.name) {
                        popUpTo(Rutas.DASHBOARD.name) { inclusive = true }
                    }
                }
            )
        }

        // 6. Pantalla de Fiados (Nueva Ruta)
        composable(Rutas.FIADOS.name) {
            FiadosScreen(
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.HISTORIAL.name) {
            HistorialVentasScreen(alVolver = { navController.popBackStack() })
        }
    }
}