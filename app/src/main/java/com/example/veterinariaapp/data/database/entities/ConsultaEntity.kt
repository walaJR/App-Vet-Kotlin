package com.example.veterinariaapp.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.veterinariaapp.data.model.Consulta
import com.example.veterinariaapp.data.model.EstadoConsulta
import com.example.veterinariaapp.data.model.Mascota
import com.example.veterinariaapp.data.model.TipoServicio
import com.example.veterinariaapp.data.model.Veterinario
import org.threeten.bp.LocalDateTime

@Entity(
    tableName = "consultas",
    foreignKeys = [
        ForeignKey(
            entity = MascotaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mascotaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["mascotaId"])]
)
data class ConsultaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mascotaId: Long,
    val tipoServicio: TipoServicio,
    val fechaHora: LocalDateTime,
    val motivo: String,
    val diagnostico: String,
    val tratamiento: String,
    val costoTotal: Double,
    val estado: EstadoConsulta,
    val veterinarioNombre: String,
    val veterinarioEspecialidad: String
) {
    fun toConsulta(mascota: Mascota): Consulta {
        return Consulta(
            mascota = mascota,
            veterinario = Veterinario(veterinarioNombre, veterinarioEspecialidad),
            tipoServicio = tipoServicio,
            descripcion = motivo,
            tiempoMinutos = 30,
            fechaHora = fechaHora,
            estado = estado,
            costoTotal = costoTotal,
            descuentoAplicado = false
        )
    }


    companion object {
        fun fromConsulta(consulta: Consulta, mascotaId: Long): ConsultaEntity {
            return ConsultaEntity(
                mascotaId = mascotaId,
                tipoServicio = consulta.tipoServicio,
                fechaHora = consulta.fechaHora,
                motivo = "Consulta veterinaria",
                diagnostico = "Diagnóstico pendiente",
                tratamiento = "Tratamiento pendiente",
                costoTotal = consulta.costoTotal,
                estado = consulta.estado,
                veterinarioNombre = consulta.veterinario.nombre,
                veterinarioEspecialidad = consulta.veterinario.especialidad
            )
        }
    }
}