package com.example.veterinariaapp.data.model

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

data class Consulta(
    val id: Int = 0,
    val mascota: Mascota,
    val veterinario: Veterinario,
    val tipoServicio: TipoServicio,
    val descripcion: String,
    val tiempoMinutos: Int,
    val fechaHora: LocalDateTime,
    var estado: EstadoConsulta = EstadoConsulta.PENDIENTE,
    var costoTotal: Double = 0.0,
    var descuentoAplicado: Boolean = false
) {
    init {
        if (costoTotal == 0.0) {
            calcularCosto()
        }
    }

    private fun calcularCosto() {
        var costo = tipoServicio.costoBase
        if (tiempoMinutos > 30) {
            costo += (tiempoMinutos - 30) * 500.0
        }
        costoTotal = costo
    }

    fun aplicarDescuento(porcentaje: Double) {
        costoTotal -= costoTotal * (porcentaje / 100)
        descuentoAplicado = true
    }

    fun formatearFecha(): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        return fechaHora.format(formatter)
    }

    override fun toString(): String {
        return "${mascota.nombre} - ${tipoServicio.displayName} - $${String.format("%.0f", costoTotal)}"
    }

    // Para compartir por intent implícito
    fun toShareText(): String {
        return """
            |📋 CONSULTA VETERINARIA
            |
            |Mascota: ${mascota.nombre}
            |Dueño: ${mascota.dueno.nombre}
            |Veterinario: ${veterinario.nombre}
            |Servicio: ${tipoServicio.displayName}
            |Descripción: $descripcion
            |Fecha: ${formatearFecha()}
            |Costo: $${String.format("%.0f", costoTotal)}
            |${if (descuentoAplicado) "Descuento aplicado (15%)" else ""}
        """.trimMargin()
    }
}