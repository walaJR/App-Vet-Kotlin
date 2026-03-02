package com.example.veterinariaapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinariaapp.data.repository.VeterinariaRepositoryRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VeterinariaRepositoryRoom.getInstance(application)

    private val _totalMascotas = MutableStateFlow(0)
    val totalMascotas: StateFlow<Int> = _totalMascotas.asStateFlow()

    private val _totalConsultas = MutableStateFlow(0)
    val totalConsultas: StateFlow<Int> = _totalConsultas.asStateFlow()

    private val _ultimoDueno = MutableStateFlow("Sin registros")
    val ultimoDueno: StateFlow<String> = _ultimoDueno.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarResumen()
    }

    fun cargarResumen() {
        viewModelScope.launch {
            _isLoading.value = true

            launch {
                repository.totalMascotas.collect { total ->
                    _totalMascotas.value = total
                }
            }

            launch {
                repository.totalConsultas.collect { total ->
                    _totalConsultas.value = total
                }
            }

            launch {
                repository.duenos.collect { duenos ->
                    _ultimoDueno.value = duenos.lastOrNull()?.nombre ?: "Sin registros"
                }
            }

            _isLoading.value = false
        }
    }
}