package com.example.veterinariaapp.data.model

data class Dueno(
    val nombre: String,
    val telefono: String,
    val email: String
) {
    override fun toString(): String {
        return "Dueño: $nombre | Tel: $telefono | Email: $email"
    }
}
