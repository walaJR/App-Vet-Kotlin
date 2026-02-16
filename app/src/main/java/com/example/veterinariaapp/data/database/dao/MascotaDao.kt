package com.example.veterinariaapp.data.database.dao

import androidx.room.*
import com.example.veterinariaapp.data.database.entities.MascotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {

    @Query("SELECT * FROM mascotas ORDER BY nombre ASC")
    fun getAllMascotas(): Flow<List<MascotaEntity>>

    @Query("SELECT * FROM mascotas WHERE id = :id")
    suspend fun getMascotaById(id: Long): MascotaEntity?

    @Query("SELECT * FROM mascotas WHERE duenoId = :duenoId")
    fun getMascotasByDueno(duenoId: Long): Flow<List<MascotaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMascota(mascota: MascotaEntity): Long

    @Update
    suspend fun updateMascota(mascota: MascotaEntity)

    @Delete
    suspend fun deleteMascota(mascota: MascotaEntity)

    @Query("DELETE FROM mascotas")
    suspend fun deleteAllMascotas()

    @Query("SELECT COUNT(*) FROM mascotas")
    fun getCountMascotas(): Flow<Int>
}