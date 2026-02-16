package com.example.veterinariaapp.data.database

import androidx.room.TypeConverter
import com.example.veterinariaapp.data.model.EstadoConsulta
import com.example.veterinariaapp.data.model.TipoServicio
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Converters {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            try {
                LocalDateTime.parse(it, formatter)
            } catch (e: Exception) {
                null
            }
        }
    }

    @TypeConverter
    fun fromTipoServicio(value: TipoServicio?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTipoServicio(value: String?): TipoServicio? {
        return value?.let {
            try {
                TipoServicio.valueOf(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    @TypeConverter
    fun fromEstadoConsulta(value: EstadoConsulta?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEstadoConsulta(value: String?): EstadoConsulta? {
        return value?.let {
            try {
                EstadoConsulta.valueOf(it)
            } catch (e: Exception) {
                null
            }
        }
    }
}