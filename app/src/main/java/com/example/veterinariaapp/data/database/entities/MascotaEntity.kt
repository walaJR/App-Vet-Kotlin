package com.example.veterinariaapp.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.veterinariaapp.data.model.Dueno
import com.example.veterinariaapp.data.model.Mascota

@Entity(
    tableName = "mascotas",
    foreignKeys = [
        ForeignKey(
            entity = DuenoEntity::class,
            parentColumns = ["id"],
            childColumns = ["duenoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["duenoId"])]
)
data class MascotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val especie: String,
    val edad: Int,
    val peso: Double,
    val duenoId: Long
) {
    fun toMascota(dueno: Dueno): Mascota {
        return Mascota(
            nombre = nombre,
            especie = especie,
            edad = edad,
            peso = peso,
            dueno = dueno
        )
    }

    companion object {
        fun fromMascota(mascota: Mascota, duenoId: Long): MascotaEntity {
            return MascotaEntity(
                nombre = mascota.nombre,
                especie = mascota.especie,
                edad = mascota.edad,
                peso = mascota.peso,
                duenoId = duenoId
            )
        }
    }
}