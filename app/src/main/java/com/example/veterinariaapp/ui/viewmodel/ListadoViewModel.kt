package com.example.veterinariaapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinariaapp.data.model.Consulta
import com.example.veterinariaapp.data.model.EstadoConsulta
import com.example.veterinariaapp.data.model.Mascota
import com.example.veterinariaapp.data.repository.VeterinariaRepositoryRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference

class ListadoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VeterinariaRepositoryRoom.getInstance(application)
    private val TAG = "ListadoViewModel"

    // Usar WeakReference para evitar memory leaks con el contexto
    private var contextRef: WeakReference<Context>? = null

    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    val consultas: StateFlow<List<Consulta>> = _consultas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarDatos()
    }

    /**
     * Carga con Coroutines y sin retener contextos
     */
    fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Cargando datos desde Room...")

            try {
                // Cargar mascotas
                launch(Dispatchers.IO) {
                    try {
                        repository.obtenerTodasLasMascotas().collect { lista ->
                            _mascotas.value = lista
                            Log.d(TAG, "Mascotas cargadas: ${lista.size}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al cargar mascotas", e)
                    }
                }

                // Cargar consultas
                launch(Dispatchers.IO) {
                    try {
                        repository.obtenerTodasLasConsultas().collect { lista ->
                            _consultas.value = lista
                            Log.d(TAG, "Consultas cargadas: ${lista.size}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al cargar consultas", e)
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarMascota(mascota: Mascota) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Eliminando mascota: ${mascota.nombre}")
                repository.eliminarMascota(mascota)
                Log.d(TAG, "Mascota eliminada correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar mascota", e)
            }
        }
    }

    fun actualizarMascota(mascota: Mascota) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Actualizando mascota: ${mascota.nombre}")
                repository.actualizarMascota(mascota)
                Log.d(TAG, "Mascota actualizada correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar mascota", e)
            }
        }
    }

    fun eliminarConsulta(consulta: Consulta) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Eliminando consulta: ${consulta.id}")
                repository.eliminarConsulta(consulta)
                Log.d(TAG, "Consulta eliminada correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar consulta", e)
            }
        }
    }

    fun cambiarEstadoConsulta(consulta: Consulta) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nuevoEstado = when (consulta.estado) {
                    EstadoConsulta.PENDIENTE -> EstadoConsulta.REALIZADO
                    EstadoConsulta.REALIZADO -> EstadoConsulta.CANCELADO
                    EstadoConsulta.CANCELADO -> EstadoConsulta.PENDIENTE
                }

                val consultaActualizada = Consulta(
                    id = consulta.id,
                    mascota = consulta.mascota,
                    veterinario = consulta.veterinario,
                    tipoServicio = consulta.tipoServicio,
                    descripcion = consulta.descripcion,
                    tiempoMinutos = consulta.tiempoMinutos,
                    fechaHora = consulta.fechaHora,
                    estado = nuevoEstado,
                    costoTotal = consulta.costoTotal,
                    descuentoAplicado = consulta.descuentoAplicado
                )

                repository.actualizarConsultaCompleta(consultaActualizada)
                Log.d(TAG, "Estado actualizado a $nuevoEstado")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cambiar estado", e)
            }
        }
    }

    /**
     * Usar ApplicationContext en lugar de Activity Context
     * para evitar memory leaks
     */
    fun compartirConsulta(context: Context, consulta: Consulta) {
        // Guardar referencia débil al contexto
        contextRef = WeakReference(context.applicationContext)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val shareText = generarTextoCompartir(consulta, formatter)

                // Usar el contexto de forma segura
                contextRef?.get()?.let { ctx ->
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        putExtra(Intent.EXTRA_SUBJECT, "Consulta Veterinaria - ${consulta.mascota.nombre}")
                        type = "text/plain"
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    withContext(Dispatchers.Main) {
                        ctx.startActivity(Intent.createChooser(shareIntent, "Compartir consulta mediante...").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    }
                    Log.d(TAG, "Intent de compartir lanzado")
                } ?: Log.w(TAG, "Contexto ya no disponible")

            } catch (e: Exception) {
                Log.e(TAG, "Error al compartir consulta", e)
            }
        }
    }

    private fun generarTextoCompartir(consulta: Consulta, formatter: DateTimeFormatter): String {
        return """
            CONSULTA VETERINARIA
            
            DATOS DE LA MASCOTA
            • Nombre: ${consulta.mascota.nombre}
            • Especie: ${consulta.mascota.especie}
            • Edad: ${consulta.mascota.edad} años
            • Dueño: ${consulta.mascota.dueno.nombre}
            
            DATOS DE LA CONSULTA
            • Servicio: ${consulta.tipoServicio.displayName}
            • Fecha: ${consulta.fechaHora.format(formatter)}
            • Veterinario: Dr. ${consulta.veterinario.nombre}
            • Estado: ${consulta.estado}
            
            Costo Total: $${String.format("%,.0f", consulta.costoTotal)}
            
            DESCRIPCIÓN
            ${consulta.descripcion}
            
            DURACIÓN
            ${consulta.tiempoMinutos} minutos
            
            ${if (consulta.descuentoAplicado) "✅ Descuento aplicado (15%)" else ""}
            
            ---
            Generado por Sistema Veterinaria V4
        """.trimIndent()
    }

    /**
     * Limpiar recursos al destruirse el ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        contextRef?.clear()
        contextRef = null
        Log.d(TAG, "🧹 ViewModel limpiado correctamente")
    }
}