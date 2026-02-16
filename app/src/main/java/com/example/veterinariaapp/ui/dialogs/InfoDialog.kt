package com.example.veterinariaapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.veterinariaapp.R

class InfoDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val iconResId: Int = R.drawable.ic_info
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_info)

        setupViews()
    }

    private fun setupViews() {
        val ivIcon = findViewById<ImageView>(R.id.ivDialogIcon)
        val tvTitle = findViewById<TextView>(R.id.tvInfoDialogTitle)
        val tvMessage = findViewById<TextView>(R.id.tvInfoDialogMessage)
        val btnOk = findViewById<Button>(R.id.btnInfoDialogOk)

        ivIcon.setImageResource(iconResId)
        tvTitle.text = title
        tvMessage.text = message

        // Accesibilidad
        tvTitle.contentDescription = "Título: $title"
        tvMessage.contentDescription = message
        btnOk.contentDescription = "Aceptar"

        btnOk.setOnClickListener { dismiss() }
    }
}