package com.example.veterinariaapp.data.database.dao

import androidx.room.*
import com.example.veterinariaapp.data.database.entities.ConsultaEntity
import com.example.veterinariaapp.data.model.EstadoConsulta
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultaDao {

    @Query("SELECT * FROM consultas ORDER BY fechaHora DESC")
    fun getAllConsultas(): Flow<List<ConsultaEntity>>

    @Query("SELECT * FROM consultas WHERE id = :id")
    suspend fun getConsultaById(id: Long): ConsultaEntity?

    @Query("SELECT * FROM consultas WHERE mascotaId = :mascotaId ORDER BY fechaHora DESC")
    fun getConsultasByMascota(mascotaId: Long): Flow<List<ConsultaEntity>>

    @Query("SELECT * FROM consultas WHERE estado = :estado ORDER BY fechaHora DESC")
    fun getConsultasByEstado(estado: EstadoConsulta): Flow<List<ConsultaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsulta(consulta: ConsultaEntity): Long

    @Update
    suspend fun updateConsulta(consulta: ConsultaEntity)

    @Delete
    suspend fun deleteConsulta(consulta: ConsultaEntity)

    @Query("DELETE FROM consultas")
    suspend fun deleteAllConsultas()

    @Query("SELECT COUNT(*) FROM consultas")
    fun getCountConsultas(): Flow<Int>

    @Query("SELECT COUNT(*) FROM consultas WHERE estado = :estado")
    fun getCountConsultasByEstado(estado: EstadoConsulta): Flow<Int>
}