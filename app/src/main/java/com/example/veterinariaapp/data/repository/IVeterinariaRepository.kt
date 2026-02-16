package com.example.veterinariaapp.data.repository

import com.example.veterinariaapp.data.model.*
import kotlinx.coroutines.flow.StateFlow

interface IVeterinariaRepository {
    val mascotas: StateFlow<List<Mascota>>
    val duenos: StateFlow<List<Dueno>>
    val consultas: StateFlow<List<Consulta>>
    val veterinarios: StateFlow<List<Veterinario>>

    fun agregarMascota(mascota: Mascota)
    fun agregarConsulta(consulta: Consulta)
    fun actualizarConsulta(consulta: Consulta)
    fun eliminarConsulta(id: Int)
    fun obtenerConsultaPorId(id: Int): Consulta?
    fun obtenerUltimoDueno(): String
    fun getTotalMascotas(): Int
    fun getTotalConsultas(): Int
    suspend fun registrarMascota(mascota: Mascota)
    suspend fun registrarConsulta(consulta: Consulta)
}