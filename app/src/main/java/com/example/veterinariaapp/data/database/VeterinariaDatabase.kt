package com.example.veterinariaapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.veterinariaapp.data.database.dao.ConsultaDao
import com.example.veterinariaapp.data.database.dao.DuenoDao
import com.example.veterinariaapp.data.database.dao.MascotaDao
import com.example.veterinariaapp.data.database.entities.ConsultaEntity
import com.example.veterinariaapp.data.database.entities.DuenoEntity
import com.example.veterinariaapp.data.database.entities.MascotaEntity

@Database(
    entities = [
        DuenoEntity::class,
        MascotaEntity::class,
        ConsultaEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VeterinariaDatabase : RoomDatabase() {

    abstract fun duenoDao(): DuenoDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun consultaDao(): ConsultaDao

    companion object {
        @Volatile
        private var INSTANCE: VeterinariaDatabase? = null

        fun getDatabase(context: Context): VeterinariaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VeterinariaDatabase::class.java,
                    "veterinaria_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}