package com.pi6u89.ventaseym.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Rutas { LOGIN, CAJA, DASHBOARD, MENU }

@Composable
fun EnrutadorApp() {
    val navController = rememberNavController()
    // Creamos el ViewModel aquí para que sea único en toda la app
    val ventasViewModel: VentasViewModel = viewModel()

    NavHost(navController = navController, startDestination = Rutas.LOGIN.name) {

        composable(Rutas.LOGIN.name) {
            LoginScreen(
                alIniciarSesionExitoso = { idUsuarioFirebase ->
                    // Pasamos a CajaScreen y enviamos el ID de usuario si es necesario
                    navController.navigate(Rutas.CAJA.name)
                }
            )
        }

        composable(Rutas.CAJA.name) {
            CajaScreen(
                viewModel = ventasViewModel,
                idUsuarioFirebase = "USUARIO_LOGUEADO", // Aquí iría el ID real de Firebase
                alAbrirCaja = {
                    navController.navigate(Rutas.DASHBOARD.name)
                }
            )
        }

        composable(Rutas.DASHBOARD.name) {
            DashboardScreen(
                viewModel = ventasViewModel,
                alSeleccionarMesa = { numeroMesa ->
                    navController.navigate(Rutas.MENU.name)
                },
                alSeleccionarParaLlevar = {
                    navController.navigate(Rutas.MENU.name)
                }
            )
        }

        composable(Rutas.MENU.name) {
            MenuScreen(
                viewModel = ventasViewModel,
                numeroMesa = 1, // Se puede dinamizar luego
                alFinalizarVenta = {
                    navController.popBackStack()
                }
            )
        }
    }
}