package com.example.veterinariaapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.veterinariaapp.R
import com.example.veterinariaapp.data.model.Dueno
import com.example.veterinariaapp.data.model.Mascota

class EditMascotaDialog(
    context: Context,
    private val mascota: Mascota,
    private val onSave: (Mascota) -> Unit
) : Dialog(context) {

    private lateinit var etNombre: EditText
    private lateinit var etEspecie: EditText
    private lateinit var etEdad: EditText
    private lateinit var etPeso: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_edit_mascota)

        setupViews()
        loadData()
    }

    private fun setupViews() {
        etNombre = findViewById(R.id.etNombreMascotaDialog)
        etEspecie = findViewById(R.id.etEspecieMascotaDialog)
        etEdad = findViewById(R.id.etEdadMascotaDialog)
        etPeso = findViewById(R.id.etPesoMascotaDialog)

        val btnCancel = findViewById<Button>(R.id.btnCancelEditMascota)
        val btnSave = findViewById<Button>(R.id.btnSaveEditMascota)

        // Accesibilidad
        etNombre.contentDescription = "Nombre de la mascota"
        etEspecie.contentDescription = "Especie de la mascota"
        etEdad.contentDescription = "Edad de la mascota en años"
        etPeso.contentDescription = "Peso de la mascota en kilogramos"

        btnCancel.setOnClickListener { dismiss() }
        btnSave.setOnClickListener { validateAndSave() }
    }

    private fun loadData() {
        etNombre.setText(mascota.nombre)
        etEspecie.setText(mascota.especie)
        etEdad.setText(mascota.edad.toString())
        etPeso.setText(mascota.peso.toString())
    }

    private fun validateAndSave() {
        val nombre = etNombre.text.toString().trim()
        val especie = etEspecie.text.toString().trim()
        val edadStr = etEdad.text.toString().trim()
        val pesoStr = etPeso.text.toString().trim()

        when {
            nombre.isEmpty() -> {
                etNombre.error = "Ingrese el nombre"
                etNombre.requestFocus()
            }
            especie.isEmpty() -> {
                etEspecie.error = "Ingrese la especie"
                etEspecie.requestFocus()
            }
            edadStr.isEmpty() -> {
                etEdad.error = "Ingrese la edad"
                etEdad.requestFocus()
            }
            pesoStr.isEmpty() -> {
                etPeso.error = "Ingrese el peso"
                etPeso.requestFocus()
            }
            else -> {
                try {
                    val edad = edadStr.toInt()
                    val peso = pesoStr.toDouble()

                    if (edad < 0 || edad > 50) {
                        Toast.makeText(context, "Edad inválida (0-50)", Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (peso <= 0 || peso > 500) {
                        Toast.makeText(context, "Peso inválido (0-500)", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val mascotaActualizada = Mascota(
                        nombre = nombre,
                        especie = especie,
                        edad = edad,
                        peso = peso,
                        dueno = mascota.dueno
                    )

                    onSave(mascotaActualizada)
                    dismiss()

                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}