package com.example.veterinariaapp.data.repository

import android.content.Context
import com.example.veterinariaapp.data.database.VeterinariaDatabase
import com.example.veterinariaapp.data.database.entities.ConsultaEntity
import com.example.veterinariaapp.data.database.entities.DuenoEntity
import com.example.veterinariaapp.data.database.entities.MascotaEntity
import com.example.veterinariaapp.data.model.Consulta
import com.example.veterinariaapp.data.model.Dueno
import com.example.veterinariaapp.data.model.EstadoConsulta
import com.example.veterinariaapp.data.model.Mascota
import com.example.veterinariaapp.data.model.Veterinario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class VeterinariaRepositoryRoom private constructor(context: Context) : IVeterinariaRepository {

    private val database = VeterinariaDatabase.getDatabase(context)
    private val duenoDao = database.duenoDao()
    private val mascotaDao = database.mascotaDao()
    private val consultaDao = database.consultaDao()

    // StateFlows para la interfaz
    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    private val _duenos = MutableStateFlow<List<Dueno>>(emptyList())
    override val duenos: StateFlow<List<Dueno>> = _duenos.asStateFlow()

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    override val consultas: StateFlow<List<Consulta>> = _consultas.asStateFlow()

    private val _veterinarios = MutableStateFlow<List<Veterinario>>(
        listOf(
            Veterinario(nombre = "Ana García", especialidad = "Cirugía General"),
            Veterinario(nombre = "Carlos Ruiz", especialidad = "Dermatología"),
            Veterinario(nombre = "María López", especialidad = "Medicina Interna"),
            Veterinario(nombre = "Pedro Martínez", especialidad = "Cardiología"),
            Veterinario(nombre = "Laura Fernández", especialidad = "Oftalmología")
        )
    )
    override val veterinarios: StateFlow<List<Veterinario>> = _veterinarios.asStateFlow()

    val totalMascotas: Flow<Int> = mascotaDao.getCountMascotas()
    val totalConsultas: Flow<Int> = consultaDao.getCountConsultas()
    val totalDuenos: Flow<Int> = duenoDao.getCountDuenos()

    fun obtenerTodasLasMascotas(): Flow<List<Mascota>> {
        return mascotaDao.getAllMascotas().map { mascotasEntity ->
            mascotasEntity.mapNotNull { mascotaEntity ->
                val duenoEntity = duenoDao.getDuenoById(mascotaEntity.duenoId)
                duenoEntity?.let {
                    mascotaEntity.toMascota(it.toDueno())
                }
            }
        }
    }

    fun obtenerTodasLasConsultas(): Flow<List<Consulta>> {
        return consultaDao.getAllConsultas().map { consultasEntity ->
            consultasEntity.mapNotNull { consultaEntity ->
                val mascotaEntity = mascotaDao.getMascotaById(consultaEntity.mascotaId)
                val duenoEntity = mascotaEntity?.let { duenoDao.getDuenoById(it.duenoId) }

                if (mascotaEntity != null && duenoEntity != null) {
                    val mascota = mascotaEntity.toMascota(duenoEntity.toDueno())
                    consultaEntity.toConsulta(mascota)
                } else null
            }
        }
    }

    suspend fun eliminarMascota(mascota: Mascota) {
        val mascotasFlow = mascotaDao.getAllMascotas()
        val mascotas = mascotasFlow.first()

        val mascotaEntity = mascotas.find {
            it.nombre == mascota.nombre &&
                    it.especie == mascota.especie &&
                    it.edad == mascota.edad
        }

        mascotaEntity?.let {
            mascotaDao.deleteMascota(it)
        }
    }

    suspend fun actualizarMascota(mascota: Mascota) {
        val mascotasFlow = mascotaDao.getAllMascotas()
        val mascotas = mascotasFlow.first()

        val mascotaEntity = mascotas.find {
            it.nombre == mascota.nombre
        }

        mascotaEntity?.let { existente ->
            val duenoEntity = duenoDao.getDuenoByEmail(mascota.dueno.email)
            duenoEntity?.let { dueno ->
                val actualizada = MascotaEntity.fromMascota(mascota, dueno.id).copy(id = existente.id)
                mascotaDao.updateMascota(actualizada)
            }
        }
    }

    suspend fun eliminarConsulta(consulta: Consulta) {
        val consultaEntity = consultaDao.getConsultaById(consulta.id.toLong())
        consultaEntity?.let {
            consultaDao.deleteConsulta(it)
        }
    }

    suspend fun actualizarConsultaCompleta(consulta: Consulta) {
        val consultaEntity = consultaDao.getConsultaById(consulta.id.toLong())
        consultaEntity?.let { existente ->
            val actualizada = ConsultaEntity.fromConsulta(consulta, existente.mascotaId).copy(id = existente.id)
            consultaDao.updateConsulta(actualizada)
        }
    }

    suspend fun registrarDueno(dueno: Dueno): Long {
        return duenoDao.insertDueno(DuenoEntity.fromDueno(dueno))
    }

    suspend fun obtenerDuenoPorEmail(email: String): Dueno? {
        return duenoDao.getDuenoByEmail(email)?.toDueno()
    }

    suspend fun registrarMascotaConDueno(mascota: Mascota): Long {
        var duenoEntity = duenoDao.getDuenoByEmail(mascota.dueno.email)

        if (duenoEntity == null) {
            val duenoId = duenoDao.insertDueno(DuenoEntity.fromDueno(mascota.dueno))
            duenoEntity = duenoDao.getDuenoById(duenoId)
        }

        return duenoEntity?.let {
            mascotaDao.insertMascota(MascotaEntity.fromMascota(mascota, it.id))
        } ?: 0
    }

    suspend fun actualizarMascota(mascota: Mascota, mascotaId: Long) {
        val duenoEntity = duenoDao.getDuenoByEmail(mascota.dueno.email)
        duenoEntity?.let {
            val mascotaEntity = MascotaEntity.fromMascota(mascota, it.id).copy(id = mascotaId)
            mascotaDao.updateMascota(mascotaEntity)
        }
    }

    suspend fun eliminarMascota(mascotaId: Long) {
        mascotaDao.getMascotaById(mascotaId)?.let {
            mascotaDao.deleteMascota(it)
        }
    }

    suspend fun registrarConsultaConMascota(consulta: Consulta): Long {
        val mascotasFlow = mascotaDao.getAllMascotas()
        val mascotas = mascotasFlow.first()

        val mascotaEntity = mascotas.find {
            it.nombre == consulta.mascota.nombre
        }

        return mascotaEntity?.let {
            consultaDao.insertConsulta(ConsultaEntity.fromConsulta(consulta, it.id))
        } ?: 0
    }

    suspend fun actualizarConsulta(consulta: Consulta, consultaId: Long) {
        val consultaEntity = consultaDao.getConsultaById(consultaId)
        consultaEntity?.let {
            val updated = ConsultaEntity.fromConsulta(consulta, it.mascotaId).copy(id = consultaId)
            consultaDao.updateConsulta(updated)
        }
    }

    suspend fun eliminarConsulta(consultaId: Long) {
        consultaDao.getConsultaById(consultaId)?.let {
            consultaDao.deleteConsulta(it)
        }
    }

    fun obtenerConsultasPorEstado(estado: EstadoConsulta): Flow<List<Consulta>> {
        return consultaDao.getConsultasByEstado(estado).map { consultasEntity ->
            consultasEntity.mapNotNull { consultaEntity ->
                val mascotaEntity = mascotaDao.getMascotaById(consultaEntity.mascotaId)
                val duenoEntity = mascotaEntity?.let { duenoDao.getDuenoById(it.duenoId) }

                if (mascotaEntity != null && duenoEntity != null) {
                    val mascota = mascotaEntity.toMascota(duenoEntity.toDueno())
                    consultaEntity.toConsulta(mascota)
                } else null
            }
        }
    }

    override fun agregarMascota(mascota: Mascota) {
    }

    override fun agregarConsulta(consulta: Consulta) {
    }

    override fun actualizarConsulta(consulta: Consulta) {
    }

    override fun eliminarConsulta(id: Int) {
    }

    override fun obtenerConsultaPorId(id: Int): Consulta? {
        return null
    }

    override fun obtenerUltimoDueno(): String {
        return ""
    }

    override fun getTotalMascotas(): Int {
        return _mascotas.value.size
    }

    override fun getTotalConsultas(): Int {
        return _consultas.value.size
    }

    override suspend fun registrarMascota(mascota: Mascota) {
        registrarMascotaConDueno(mascota)
    }

    override suspend fun registrarConsulta(consulta: Consulta) {
        registrarConsultaConMascota(consulta)
    }

    companion object {
        @Volatile
        private var INSTANCE: VeterinariaRepositoryRoom? = null

        fun getInstance(context: Context): VeterinariaRepositoryRoom {
            return INSTANCE ?: synchronized(this) {
                val instance = VeterinariaRepositoryRoom(context)
                INSTANCE = instance
                instance
            }
        }
    }
}