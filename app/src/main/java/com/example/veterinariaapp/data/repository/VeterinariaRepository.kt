package com.example.veterinariaapp.data.repository

import com.example.veterinariaapp.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VeterinariaRepository : IVeterinariaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    private val _duenos = MutableStateFlow<List<Dueno>>(emptyList())
    override val duenos: StateFlow<List<Dueno>> = _duenos.asStateFlow()

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    override val consultas: StateFlow<List<Consulta>> = _consultas.asStateFlow()

    private val _veterinarios = MutableStateFlow(listOf(
        Veterinario("Dr. Carlos Ramírez", "Medicina General"),
        Veterinario("Dra. María González", "Cirugía"),
        Veterinario("Dr. Pedro Silva", "Emergencias")
    ))
    override val veterinarios: StateFlow<List<Veterinario>> = _veterinarios.asStateFlow()

    private var contadorConsultas = 1

    override fun agregarMascota(mascota: Mascota) {
        _mascotas.value = _mascotas.value + mascota
        if (!_duenos.value.any { it.email == mascota.dueno.email }) {
            _duenos.value = _duenos.value + mascota.dueno
        }
    }

    override fun agregarConsulta(consulta: Consulta) {
        val mascotasMismoDueno = _mascotas.value.filter {
            it.dueno.email == consulta.mascota.dueno.email
        }

        if (mascotasMismoDueno.size > 1 && !consulta.descuentoAplicado) {
            consulta.aplicarDescuento(15.0)
        }

        val nuevaConsulta = consulta.copy(id = contadorConsultas++)
        _consultas.value = _consultas.value + nuevaConsulta
    }

    override fun actualizarConsulta(consulta: Consulta) {
        _consultas.value = _consultas.value.map {
            if (it.id == consulta.id) consulta else it
        }
    }

    override fun eliminarConsulta(id: Int) {
        _consultas.value = _consultas.value.filter { it.id != id }
    }

    override fun obtenerConsultaPorId(id: Int): Consulta? {
        return _consultas.value.find { it.id == id }
    }

    override fun obtenerUltimoDueno(): String {
        return _duenos.value.lastOrNull()?.nombre ?: "Ninguno"
    }

    override fun getTotalMascotas(): Int = _mascotas.value.size

    override fun getTotalConsultas(): Int = _consultas.value.size

    override suspend fun registrarMascota(mascota: Mascota) {
        agregarMascota(mascota)
    }

    override suspend fun registrarConsulta(consulta: Consulta) {
        agregarConsulta(consulta)
    }

    // Singleton para mantener los datos entre Activities
    companion object {
        @Volatile
        private var INSTANCE: VeterinariaRepository? = null

        fun getInstance(): VeterinariaRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VeterinariaRepository().also { INSTANCE = it }
            }
        }
    }
}