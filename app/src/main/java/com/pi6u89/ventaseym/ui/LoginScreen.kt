package com.pi6u89.ventaseym.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pi6u89.ventaseym.red.GestorAutenticacion

/**
 * Documentación: Pantalla principal de inicio de sesión.
 */
@Composable
fun LoginScreen(
    alIniciarSesionExitoso: (String) -> Unit // Función que se ejecuta si todo sale bien
) {
    // Variables para guardar lo que el usuario escribe en tiempo real
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var estaCargando by remember { mutableStateOf(false) }

    // Instanciamos nuestro gestor de Firebase
    val gestorAuth = remember { GestorAutenticacion() }

    // Diseño de la columna principal (elementos centrados)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido a E&M", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de texto para el Correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para la Contraseña
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto con asteriscos
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra un mensaje rojo si hay un error
        if (mensajeError.isNotEmpty()) {
            Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón de Inicio de Sesión
        Button(
            onClick = {
                estaCargando = true
                mensajeError = "" // Limpiamos errores anteriores

                // Llamamos a la lógica de Firebase
                gestorAuth.iniciarSesion(
                    correo = correo.trim(),
                    contrasena = contrasena,
                    alExito = { idUsuario ->
                        estaCargando = false
                        alIniciarSesionExitoso(idUsuario) // Avisamos a la app que avanzamos
                    },
                    alError = { error ->
                        estaCargando = false
                        mensajeError = error
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaCargando // Deshabilita el botón mientras carga
        ) {
            if (estaCargando) {
                Text("Cargando...")
            } else {
                Text("Ingresar")
            }
        }
    }
}