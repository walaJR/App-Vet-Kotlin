package com.example.veterinariaapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast

class NetworkBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                if (isConnectedToWifi(context)) {
                    Toast.makeText(
                        context,
                        "Conectado a Wi-Fi - Sincronizando datos de veterinaria",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reiniciar servicio de recordatorios después de reiniciar dispositivo
                Intent(context, com.example.veterinariaapp.service.RecordatorioService::class.java).also {
                    context.startService(it)
                }
            }
        }
    }

    private fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}