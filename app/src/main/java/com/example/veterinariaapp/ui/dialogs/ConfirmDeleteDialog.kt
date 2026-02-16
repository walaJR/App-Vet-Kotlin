package com.example.veterinariaapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.veterinariaapp.R

class ConfirmDeleteDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val onConfirm: () -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_confirm_delete)

        setupViews()
    }

    private fun setupViews() {
        val tvTitle = findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = findViewById<TextView>(R.id.tvDialogMessage)
        val btnCancel = findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = findViewById<Button>(R.id.btnDialogConfirm)

        tvTitle.text = title
        tvMessage.text = message

        // Accesibilidad
        tvTitle.contentDescription = "Título: $title"
        tvMessage.contentDescription = message
        btnCancel.contentDescription = "Cancelar operación"
        btnConfirm.contentDescription = "Confirmar eliminación"

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }
    }
}