package com.example.veterinariaapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinariaapp.data.model.*
import com.example.veterinariaapp.data.repository.VeterinariaRepositoryRoom
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import java.io.IOException

class ConsultaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VeterinariaRepositoryRoom.getInstance(application)
    private val TAG = "ConsultaViewModel"

    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    val consultas: StateFlow<List<Consulta>> = _consultas.asStateFlow()

    val veterinarios = repository.veterinarios

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _consultaGuardada = MutableStateFlow(false)
    val consultaGuardada: StateFlow<Boolean> = _consultaGuardada.asStateFlow()

    private val _errorState = MutableStateFlow<ErrorState?>(null)
    val errorState: StateFlow<ErrorState?> = _errorState.asStateFlow()

    /**
     * Clase para manejo estructurado de errores
     */
    data class ErrorState(
        val message: String,
        val throwable: Throwable?,
        val timestamp: LocalDateTime = LocalDateTime.now()
    )

    init {
        cargarDatos()
    }

    /**
     * Carga de datos con Coroutines y debugging
     */
    private fun cargarDatos() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Iniciando carga de datos...")

                // Cargar mascotas en paralelo
                launch {
                    try {
                        withContext(Dispatchers.IO) {
                            repository.obtenerTodasLasMascotas().collect { lista ->
                                _mascotas.value = lista
                                Log.d(TAG, "Mascotas cargadas: ${lista.size}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al cargar mascotas", e)
                        registrarError("Error al cargar mascotas", e)
                    }
                }

                // Cargar consultas en paralelo
                launch {
                    try {
                        withContext(Dispatchers.IO) {
                            repository.obtenerTodasLasConsultas().collect { lista ->
                                _consultas.value = lista
                                Log.d(TAG, "Consultas cargadas: ${lista.size}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al cargar consultas", e)
                        registrarError("Error al cargar consultas", e)
                    }
                }

            } catch (e: CancellationException) {
                Log.w(TAG, "⚠Operación cancelada")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error general en carga de datos", e)
                registrarError("Error al cargar datos", e)
            }
        }
    }

    /**
     * Registro de consulta con manejo avanzado de errores
     */
    fun registrarConsulta(
        mascota: Mascota,
        veterinario: Veterinario,
        tipoServicio: TipoServicio,
        descripcion: String,
        tiempoMinutos: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                Log.d(TAG, "Registrando consulta para mascota: ${mascota.nombre}")

                // Validaciones con logging detallado
                validarConsulta(mascota, veterinario, descripcion, tiempoMinutos)

                // Simular procesamiento
                kotlinx.coroutines.delay(1000)

                val consulta = Consulta(
                    id = 0,
                    mascota = mascota,
                    veterinario = veterinario,
                    tipoServicio = tipoServicio,
                    descripcion = descripcion,
                    tiempoMinutos = tiempoMinutos,
                    fechaHora = LocalDateTime.now()
                )

                // Guardar en base de datos (IO)
                withContext(Dispatchers.IO) {
                    repository.registrarConsulta(consulta)
                }

                Log.d(TAG, "Consulta registrada exitosamente")
                _consultaGuardada.value = true

            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Error de validación: ${e.message}", e)
                registrarError("Validación fallida: ${e.message}", e)
            } catch (e: IOException) {
                Log.e(TAG, "Error de IO: ${e.message}", e)
                registrarError("Error de conexión o almacenamiento", e)
            } catch (e: Exception) {
                Log.e(TAG, "Error inesperado: ${e.message}", e)
                registrarError("Error al registrar consulta", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Validaciones con logging detallado
     */
    private fun validarConsulta(
        mascota: Mascota,
        veterinario: Veterinario,
        descripcion: String,
        tiempoMinutos: Int
    ) {
        Log.d(TAG, "Validando consulta...")

        when {
            descripcion.isBlank() -> {
                Log.w(TAG, "Descripción vacía")
                throw IllegalArgumentException("La descripción no puede estar vacía")
            }
            descripcion.length < 10 -> {
                Log.w(TAG, "Descripción muy corta: ${descripcion.length} caracteres")
                throw IllegalArgumentException("La descripción debe tener al menos 10 caracteres")
            }
            tiempoMinutos <= 0 -> {
                Log.w(TAG, "Tiempo inválido: $tiempoMinutos")
                throw IllegalArgumentException("El tiempo debe ser mayor a 0")
            }
            tiempoMinutos > 480 -> {
                Log.w(TAG, "Tiempo muy largo: $tiempoMinutos minutos")
                throw IllegalArgumentException("El tiempo no puede ser mayor a 8 horas")
            }
        }

        Log.d(TAG, "Validaciones pasadas correctamente")
    }

    /**
     * Sistema de registro de errores
     */
    private fun registrarError(message: String, throwable: Throwable?) {
        val errorState = ErrorState(message, throwable)
        _errorState.value = errorState

        // Log detallado para debugging
        Log.e(TAG, """
            =====================================
            ERROR REGISTRADO
            Mensaje: $message
            Tipo: ${throwable?.javaClass?.simpleName ?: "Unknown"}
            Stack trace:
            ${throwable?.stackTraceToString() ?: "No disponible"}
            =====================================
        """.trimIndent())
    }

    /**
     * Simular diferentes tipos de errores para testing
     */
    fun simularError(tipo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.w(TAG, "SIMULANDO ERROR: $tipo")
                kotlinx.coroutines.delay(800)

                when (tipo) {
                    "network" -> throw IOException("Error de conexión simulado")
                    "validation" -> throw IllegalArgumentException("Validación fallida simulada")
                    "null" -> throw NullPointerException("Referencia nula simulada")
                    else -> throw Exception("Error genérico simulado")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error capturado en simulación", e)
                registrarError("Error simulado: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarEstadoConsulta(consulta: Consulta, nuevoEstado: EstadoConsulta) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Actualizando estado de consulta ${consulta.id} a $nuevoEstado")
                val consultaActualizada = consulta.copy(estado = nuevoEstado)
                withContext(Dispatchers.IO) {
                    repository.actualizarConsultaCompleta(consultaActualizada)
                }
                Log.d(TAG, "Estado actualizado correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar estado", e)
                registrarError("Error al actualizar estado", e)
            }
        }
    }

    fun eliminarConsulta(id: Int) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Eliminando consulta $id")
                val consulta = _consultas.value.find { it.id == id }
                consulta?.let {
                    withContext(Dispatchers.IO) {
                        repository.eliminarConsulta(it)
                    }
                    Log.d(TAG, "Consulta eliminada correctamente")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar consulta", e)
                registrarError("Error al eliminar consulta", e)
            }
        }
    }

    fun resetearEstado() {
        _consultaGuardada.value = false
        _errorState.value = null
    }

    fun limpiarError() {
        _errorState.value = null
    }
}