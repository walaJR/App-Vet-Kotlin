package com.example.veterinariaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinariaapp.data.model.*
import com.example.veterinariaapp.data.repository.VeterinariaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

class VeterinariaViewModel : ViewModel() {

    private val repository = VeterinariaRepository()

    val mascotas = repository.mascotas
    val consultas = repository.consultas
    val veterinarios = repository.veterinarios
    val duenos = repository.duenos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _resumenVisible = MutableStateFlow(true)
    val resumenVisible: StateFlow<Boolean> = _resumenVisible.asStateFlow()

    fun agregarMascota(
        nombreMascota: String,
        especie: String,
        edad: Int,
        peso: Double,
        nombreDueno: String,
        telefono: String,
        email: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val dueno = Dueno(nombreDueno, telefono, email)
            val mascota = Mascota(nombreMascota, especie, edad, peso, dueno)
            repository.agregarMascota(mascota)

            _isLoading.value = false
        }
    }

    fun agregarConsulta(
        mascota: Mascota,
        veterinario: Veterinario,
        tipoServicio: TipoServicio,
        descripcion: String,
        tiempoMinutos: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val consulta = Consulta(
                id = 0,
                mascota = mascota,
                veterinario = veterinario,
                tipoServicio = tipoServicio,
                descripcion = descripcion,
                tiempoMinutos = tiempoMinutos,
                fechaHora = LocalDateTime.now()
            )

            repository.agregarConsulta(consulta)
            _isLoading.value = false
        }
    }

    fun getTotalMascotas(): Int = repository.getTotalMascotas()

    fun getTotalConsultas(): Int = repository.getTotalConsultas()

    fun getUltimoDueno(): String = repository.obtenerUltimoDueno()

    fun toggleResumenVisible() {
        _resumenVisible.value = !_resumenVisible.value
    }
}