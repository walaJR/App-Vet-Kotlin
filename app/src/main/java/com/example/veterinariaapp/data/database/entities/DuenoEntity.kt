package com.example.veterinariaapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.veterinariaapp.data.model.Dueno

@Entity(tableName = "duenos")
data class DuenoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val telefono: String,
    val email: String
) {
    fun toDueno(): Dueno {
        return Dueno(
            nombre = nombre,
            telefono = telefono,
            email = email
        )
    }

    companion object {
        fun fromDueno(dueno: Dueno): DuenoEntity {
            return DuenoEntity(
                nombre = dueno.nombre,
                telefono = dueno.telefono,
                email = dueno.email
            )
        }
    }
}