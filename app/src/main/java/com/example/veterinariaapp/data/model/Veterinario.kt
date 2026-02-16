package com.example.veterinariaapp.data.model

data class Veterinario(
    val nombre: String,
    val especialidad: String
) {
    override fun toString(): String {
        return "Dr. $nombre - $especialidad"
    }
}
