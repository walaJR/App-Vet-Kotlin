package com.example.veterinariaapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class VeterinariaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializamos ThreeTenABP para el manejo de fechas
        AndroidThreeTen.init(this)

        android.util.Log.d("VeterinariaApp", "Aplicación iniciada correctamente")
    }
}