package com.example.veterinariaapp.data.model

data class Mascota(
    val nombre: String,
    val especie: String,
    val edad: Int,
    val peso: Double,
    val dueno: Dueno
) {
    override fun toString(): String {
        return "$nombre ($especie) - ${dueno.nombre}"
    }
}
