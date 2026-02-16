package com.example.veterinariaapp.data.database.dao

import androidx.room.*
import com.example.veterinariaapp.data.database.entities.DuenoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DuenoDao {

    @Query("SELECT * FROM duenos ORDER BY nombre ASC")
    fun getAllDuenos(): Flow<List<DuenoEntity>>

    @Query("SELECT * FROM duenos WHERE id = :id")
    suspend fun getDuenoById(id: Long): DuenoEntity?

    @Query("SELECT * FROM duenos WHERE email = :email LIMIT 1")
    suspend fun getDuenoByEmail(email: String): DuenoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDueno(dueno: DuenoEntity): Long

    @Update
    suspend fun updateDueno(dueno: DuenoEntity)

    @Delete
    suspend fun deleteDueno(dueno: DuenoEntity)

    @Query("DELETE FROM duenos")
    suspend fun deleteAllDuenos()

    @Query("SELECT COUNT(*) FROM duenos")
    fun getCountDuenos(): Flow<Int>
}