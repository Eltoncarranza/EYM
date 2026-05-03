package com.pi6u89.ventaseym.modelos // Nota: Asegúrate de que este paquete coincida con el tuyo

import java.util.UUID

// --- 1. ROLES Y USUARIOS ---
enum class RolUsuario {
    CAJERA, ADMINISTRADOR
}

data class Usuario(
    val idFirebase: String,
    val correo: String,
    val rol: RolUsuario
)

// --- 2. CONTROL DE CAJA ---
data class SesionCaja(
    val id: String = UUID.randomUUID().toString(),
    val idCajera: String,
    val montoInicialEfectivo: Double = 0.0,
    var montoDeclaradoPorCajera: Double = 0.0, // Para el cierre ciego
    var montoTotalVentasEfectivo: Double = 0.0,
    var montoTotalEgresos: Double = 0.0,
    var diferencia: Double = 0.0,
    var estaAbierta: Boolean = true,
    val fechaApertura: Long = System.currentTimeMillis()
)

data class Egreso(
    val id: String = UUID.randomUUID().toString(),
    val sesionCajaId: String,
    val descripcion: String,
    val monto: Double,
    val fechaHora: Long = System.currentTimeMillis()
)

// --- 3. MENÚ Y MESAS ---
data class Producto(
    val id: String,
    val nombre: String,
    val categoria: String // "Comida" o "Cafeteria"
)

data class Mesa(
    val id: String = UUID.randomUUID().toString(),
    val numero: Int,
    var estaOcupada: Boolean = false
)

// Representa un plato dentro del carrito antes de cobrar
data class ItemPedido(
    val idUnicoItem: String = UUID.randomUUID().toString(),
    val nombrePlato: String,
    var precioIngresadoManualmente: Double
)

// --- 4. VENTAS Y MODO OFFLINE ---
enum class EstadoSincronizacion {
    PENDIENTE, SINCRONIZADO
}

data class Venta(
    val id: String = UUID.randomUUID().toString(),
    val sesionCajaId: String,
    val mesaId: String? = null,
    val esParaLlevar: Boolean = false,
    val tipoEnvase: String = "NINGUNO",

    val precioTotal: Double,
    val metodoPago: String, // "Efectivo" o "Yape"
    val montoRecibido: Double,
    val vuelto: Double,
    val nombreCliente: String,

    val esFiado: Boolean = false,
    var saldoPendiente: Double = 0.0,

    var estadoSincronizacion: EstadoSincronizacion = EstadoSincronizacion.PENDIENTE,
    val fechaHora: Long = System.currentTimeMillis()
)