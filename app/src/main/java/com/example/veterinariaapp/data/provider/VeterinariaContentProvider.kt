package com.example.veterinariaapp.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.veterinariaapp.data.repository.VeterinariaRepository

class VeterinariaContentProvider : ContentProvider() {

    private val repository by lazy { VeterinariaRepository.getInstance() }

    companion object {
        private const val AUTHORITY = "com.example.veterinariaapp.provider"
        private const val MASCOTAS = 1
        private const val CONSULTAS = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "mascotas", MASCOTAS)
            addURI(AUTHORITY, "consultas", CONSULTAS)
        }

        val CONTENT_URI_MASCOTAS: Uri = Uri.parse("content://$AUTHORITY/mascotas")
        val CONTENT_URI_CONSULTAS: Uri = Uri.parse("content://$AUTHORITY/consultas")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            MASCOTAS -> getMascotasCursor()
            CONSULTAS -> getConsultasCursor()
            else -> null
        }
    }

    private fun getMascotasCursor(): Cursor {
        val cursor = MatrixCursor(arrayOf(
            "nombre",
            "especie",
            "edad",
            "peso",
            "dueno_nombre",
            "dueno_email"
        ))

        repository.mascotas.value.forEach { mascota ->
            cursor.addRow(arrayOf(
                mascota.nombre,
                mascota.especie,
                mascota.edad,
                mascota.peso,
                mascota.dueno.nombre,
                mascota.dueno.email
            ))
        }

        return cursor
    }

    private fun getConsultasCursor(): Cursor {
        val cursor = MatrixCursor(arrayOf(
            "id",
            "mascota",
            "veterinario",
            "servicio",
            "costo",
            "estado",
            "fecha"
        ))

        repository.consultas.value.forEach { consulta ->
            cursor.addRow(arrayOf(
                consulta.id,
                consulta.mascota.nombre,
                consulta.veterinario.nombre,
                consulta.tipoServicio.displayName,
                consulta.costoTotal,
                consulta.estado.name,
                consulta.formatearFecha()
            ))
        }

        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            MASCOTAS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.mascotas"
            CONSULTAS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.consultas"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}