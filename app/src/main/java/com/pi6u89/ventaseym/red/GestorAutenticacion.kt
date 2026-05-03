package com.pi6u89.ventaseym.red

import com.google.firebase.auth.FirebaseAuth

/**
 * Documentación: Esta clase maneja la conexión directa con Firebase Authentication.
 */
class GestorAutenticacion {

    // Obtenemos la herramienta de Firebase
    private val auth = FirebaseAuth.getInstance()

    /**
     * Intenta iniciar sesión con los datos ingresados en la pantalla.
     */
    fun iniciarSesion(
        correo: String,
        contrasena: String,
        alExito: (String) -> Unit,
        alError: (String) -> Unit
    ) {
        // Validamos que los campos no estén vacíos
        if (correo.isBlank() || contrasena.isBlank()) {
            alError("El correo y la contraseña no pueden estar vacíos")
            return
        }

        // Enviamos los datos a Firebase
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    // Éxito: Extraemos el ID único del usuario
                    val idFirebase = auth.currentUser?.uid ?: ""
                    alExito(idFirebase)
                } else {
                    // Error: Puede ser contraseña incorrecta o falta de internet
                    val mensajeError = tarea.exception?.localizedMessage ?: "Error desconocido"
                    alError(mensajeError)
                }
            }
    }
}