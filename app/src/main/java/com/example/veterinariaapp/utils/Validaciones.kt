package com.example.veterinariaapp.utils

object Validaciones {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val telefonoRegex = Regex("^[+]?[0-9]{8,15}$")

    fun validarEmail(email: String): Boolean {
        return emailRegex.matches(email)
    }

    fun validarTelefono(telefono: String): Boolean {
        return telefonoRegex.matches(telefono.replace(" ", "").replace("-", ""))
    }

    fun formatearTelefono(telefono: String): String {
        return telefono.replace(" ", "").replace("-", "")
    }

    fun formatearPrecio(precio: Double): String {
        return "$${String.format("%,.0f", precio)}"
    }
}