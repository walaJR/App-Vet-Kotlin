package com.example.veterinariaapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import leakcanary.LeakCanary

class VeterinariaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar ThreeTenABP
        AndroidThreeTen.init(this)

        // Configuración personalizada de LeakCanary
        configureLeakCanary()
    }

    /**
     * Configurar LeakCanary para detección de memory leaks
     */
    private fun configureLeakCanary() {
        LeakCanary.config = LeakCanary.config.copy(
            // Retención de heapdumps
            retainedVisibleThreshold = 3,
            // Mostrar notificación inmediata
            dumpHeap = true
        )

        android.util.Log.d("VeterinariaApp", "LeakCanary configurado correctamente")
    }
}