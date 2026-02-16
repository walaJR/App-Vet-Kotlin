package com.example.veterinariaapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinariaapp.data.model.Dueno
import com.example.veterinariaapp.data.model.Mascota
import com.example.veterinariaapp.data.repository.VeterinariaRepositoryRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VeterinariaRepositoryRoom.getInstance(application)
    private val TAG = "RegistroViewModel"

    // Estados del formulario - Datos de la Mascota
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _especie = MutableStateFlow("")
    val especie: StateFlow<String> = _especie.asStateFlow()

    private val _edad = MutableStateFlow("")
    val edad: StateFlow<String> = _edad.asStateFlow()

    private val _peso = MutableStateFlow("")
    val peso: StateFlow<String> = _peso.asStateFlow()

    // Estados del formulario - Datos del Dueño
    private val _nombreDueno = MutableStateFlow("")
    val nombreDueno: StateFlow<String> = _nombreDueno.asStateFlow()

    private val _telefonoDueno = MutableStateFlow("")
    val telefonoDueno: StateFlow<String> = _telefonoDueno.asStateFlow()

    private val _emailDueno = MutableStateFlow("")
    val emailDueno: StateFlow<String> = _emailDueno.asStateFlow()

    // Estados de UI
    private val _especieExpandida = MutableStateFlow(false)
    val especieExpandida: StateFlow<Boolean> = _especieExpandida.asStateFlow()

    // Estados para la UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mascotaRegistrada = MutableStateFlow(false)
    val mascotaRegistrada: StateFlow<Boolean> = _mascotaRegistrada.asStateFlow()

    // Alias para compatibilidad con RegistroActivity
    val registroExitoso: StateFlow<Boolean> = _mascotaRegistrada

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    // Funciones de cambio de estado (para los campos del formulario)
    fun onNombreChange(value: String) { _nombre.value = value }
    fun onEspecieChange(value: String) { _especie.value = value }
    fun onEdadChange(value: String) { _edad.value = value }
    fun onPesoChange(value: String) { _peso.value = value }
    fun onNombreDuenoChange(value: String) { _nombreDueno.value = value }
    fun onTelefonoDuenoChange(value: String) { _telefonoDueno.value = value }
    fun onEmailDuenoChange(value: String) { _emailDueno.value = value }
    fun onEspecieExpandidaChange(value: Boolean) { _especieExpandida.value = value }

    /**
     * Operación en segundo plano con Coroutines
     */
    fun registrarMascota() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _progress.value = 0f

            try {
                Log.d(TAG, "Iniciando registro de mascota: ${_nombre.value}")

                // Validar que los campos no estén vacíos
                if (_nombre.value.isBlank()) {
                    throw IllegalArgumentException("El nombre de la mascota es requerido")
                }
                if (_especie.value.isBlank()) {
                    throw IllegalArgumentException("Seleccione una especie")
                }
                if (_edad.value.isBlank()) {
                    throw IllegalArgumentException("La edad es requerida")
                }
                if (_peso.value.isBlank()) {
                    throw IllegalArgumentException("El peso es requerido")
                }
                if (_nombreDueno.value.isBlank()) {
                    throw IllegalArgumentException("El nombre del dueño es requerido")
                }
                if (_telefonoDueno.value.isBlank()) {
                    throw IllegalArgumentException("El teléfono es requerido")
                }
                if (_emailDueno.value.isBlank()) {
                    throw IllegalArgumentException("El email es requerido")
                }

                _progress.value = 0.2f

                // Convertir tipos
                val edadInt = _edad.value.toIntOrNull() ?: throw IllegalArgumentException("Edad inválida")
                val pesoDouble = _peso.value.toDoubleOrNull() ?: throw IllegalArgumentException("Peso inválido")

                // Validaciones con la función existente
                val dueno = Dueno(
                    nombre = _nombreDueno.value.trim(),
                    telefono = _telefonoDueno.value.trim(),
                    email = _emailDueno.value.trim()
                )

                withContext(Dispatchers.Default) {
                    validarDatos(_nombre.value, _especie.value, edadInt, pesoDouble, dueno)
                }

                _progress.value = 0.4f
                Log.d(TAG, "Validaciones completadas")

                // Simular procesamiento de datos
                delay(500)
                _progress.value = 0.6f

                // Verificar si el dueño ya existe (operación IO)
                val duenoExistente = withContext(Dispatchers.IO) {
                    repository.obtenerDuenoPorEmail(dueno.email)
                }
                Log.d(TAG, "Dueño verificado: ${duenoExistente?.nombre ?: "Nuevo"}")

                // Crear objeto mascota
                val mascota = Mascota(
                    nombre = _nombre.value.trim(),
                    especie = _especie.value,
                    edad = edadInt,
                    peso = pesoDouble,
                    dueno = dueno
                )

                _progress.value = 0.8f

                // Registrar en base de datos (operación IO)
                val mascotaId = withContext(Dispatchers.IO) {
                    repository.registrarMascotaConDueno(mascota)
                }

                _progress.value = 1f

                if (mascotaId > 0) {
                    Log.d(TAG, "Mascota registrada exitosamente con ID: $mascotaId")
                    _mascotaRegistrada.value = true
                    limpiarFormulario()
                } else {
                    throw Exception("No se pudo registrar la mascota en la base de datos")
                }

            } catch (e: IllegalArgumentException) {
                // Error de validación
                Log.e(TAG, "Error de validación: ${e.message}", e)
                _errorMessage.value = "Error de validación: ${e.message}"
            } catch (e: NumberFormatException) {
                Log.e(TAG, "Error de formato numérico", e)
                _errorMessage.value = "Edad o peso inválidos"
            } catch (e: Exception) {
                // Error general
                Log.e(TAG, "Error al registrar mascota: ${e.message}", e)
                _errorMessage.value = "Error al registrar: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Operación en segundo plano con Coroutines (sobrecarga original)
     */
    fun registrarMascota(
        nombre: String,
        especie: String,
        edad: Int,
        peso: Double,
        dueno: Dueno
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _progress.value = 0f

            try {
                Log.d(TAG, "Iniciando registro de mascota: $nombre")

                // Validaciones
                withContext(Dispatchers.Default) {
                    validarDatos(nombre, especie, edad, peso, dueno)
                }

                _progress.value = 0.2f

                // Simular procesamiento de datos
                delay(500)
                _progress.value = 0.4f
                Log.d(TAG, "Validaciones completadas")

                // Verificar si el dueño ya existe (operación IO)
                val duenoExistente = withContext(Dispatchers.IO) {
                    repository.obtenerDuenoPorEmail(dueno.email)
                }

                _progress.value = 0.6f
                Log.d(TAG, "Dueño verificado: ${duenoExistente?.nombre ?: "Nuevo"}")

                // Crear objeto mascota
                val mascota = Mascota(
                    nombre = nombre,
                    especie = especie,
                    edad = edad,
                    peso = peso,
                    dueno = dueno
                )

                _progress.value = 0.8f

                // Registrar en base de datos (operación IO)
                val mascotaId = withContext(Dispatchers.IO) {
                    repository.registrarMascotaConDueno(mascota)
                }

                _progress.value = 1f

                if (mascotaId > 0) {
                    Log.d(TAG, "Mascota registrada exitosamente con ID: $mascotaId")
                    _mascotaRegistrada.value = true
                } else {
                    throw Exception("No se pudo registrar la mascota en la base de datos")
                }

            } catch (e: IllegalArgumentException) {
                // Error de validación
                Log.e(TAG, "Error de validación: ${e.message}", e)
                _errorMessage.value = "Error de validación: ${e.message}"
            } catch (e: Exception) {
                // Error general
                Log.e(TAG, "Error al registrar mascota: ${e.message}", e)
                _errorMessage.value = "Error al registrar: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Validaciones con try-catch
     */
    private fun validarDatos(
        nombre: String,
        especie: String,
        edad: Int,
        peso: Double,
        dueno: Dueno
    ) {
        try {
            when {
                nombre.isBlank() -> throw IllegalArgumentException("El nombre no puede estar vacío")
                nombre.length < 2 -> throw IllegalArgumentException("El nombre debe tener al menos 2 caracteres")
                especie.isBlank() -> throw IllegalArgumentException("La especie no puede estar vacía")
                edad < 0 -> throw IllegalArgumentException("La edad no puede ser negativa")
                edad > 30 -> throw IllegalArgumentException("La edad no puede ser mayor a 30 años")
                peso <= 0 -> throw IllegalArgumentException("El peso debe ser mayor a 0")
                peso > 500 -> throw IllegalArgumentException("El peso no puede ser mayor a 500 kg")
                dueno.nombre.isBlank() -> throw IllegalArgumentException("El nombre del dueño no puede estar vacío")
                dueno.email.isBlank() -> throw IllegalArgumentException("El email no puede estar vacío")
                !dueno.email.contains("@") -> throw IllegalArgumentException("Email inválido")
                dueno.telefono.isBlank() -> throw IllegalArgumentException("El teléfono no puede estar vacío")
                dueno.telefono.length < 9 -> throw IllegalArgumentException("Teléfono inválido (mínimo 9 dígitos)")
            }

            Log.d(TAG, "Todos los datos son válidos")
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Validación fallida: ${e.message}")
            throw e
        }
    }

    /**
     * Simular error para testing
     */
    fun simularError() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.w(TAG, "SIMULANDO ERROR INTENCIONAL")
                delay(1000)
                throw Exception("Error simulado para testing de debugging")
            } catch (e: Exception) {
                Log.e(TAG, "Error capturado: ${e.message}", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun limpiarFormulario() {
        _nombre.value = ""
        _especie.value = ""
        _edad.value = ""
        _peso.value = ""
        _nombreDueno.value = ""
        _telefonoDueno.value = ""
        _emailDueno.value = ""
    }

    fun resetearEstado() {
        _mascotaRegistrada.value = false
        _errorMessage.value = null
        _progress.value = 0f
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}