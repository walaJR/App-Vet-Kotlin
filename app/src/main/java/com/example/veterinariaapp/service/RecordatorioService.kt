package com.example.veterinariaapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.veterinariaapp.MainActivity
import com.example.veterinariaapp.data.model.EstadoConsulta
import com.example.veterinariaapp.data.repository.VeterinariaRepository
import kotlinx.coroutines.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit

class RecordatorioService : Service() {

    private val repository = VeterinariaRepository.getInstance()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    companion object {
        private const val CHANNEL_ID = "veterinaria_recordatorios"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("RecordatorioService", "Service creado")
        createNotificationChannel()

        // ENVIAMOS NOTIFICACIÓN DE PRUEBA INMEDIATAMENTE
        enviarNotificacion(
            "🐾 Recordatorio de Consulta",
            "Consulta para Violeta programada para mañana a las 10:00 AM"
        )

        Log.d("RecordatorioService", "Notificación enviada")

        // Iniciamos verificación periódica de consultas pendientes
        serviceScope.launch {
            while (isActive) {
                verificarConsultasPendientes()
                delay(3600000) // Revisar cada hora
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("RecordatorioService", "onStartCommand llamado")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RecordatorioService", "Service destruido")
        serviceScope.cancel()
    }

    private suspend fun verificarConsultasPendientes() {
        withContext(Dispatchers.Main) {
            repository.consultas.value
                .filter { it.estado == EstadoConsulta.PENDIENTE }
                .forEach { consulta ->
                    val horasHasta = ChronoUnit.HOURS.between(LocalDateTime.now(), consulta.fechaHora)

                    if (horasHasta in 1..24) {
                        enviarNotificacion(
                            "Recordatorio de Consulta",
                            "Consulta para ${consulta.mascota.nombre} mañana a las ${consulta.fechaHora.hour}:00"
                        )
                    }
                }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios Veterinaria"
            val descriptionText = "Notificaciones de consultas programadas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("RecordatorioService", "Canal de notificaciones creado")
        }
    }

    fun enviarNotificacion(titulo: String, mensaje: String) {
        Log.d("RecordatorioService", "Intentando enviar notificación: $titulo")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())

        Log.d("RecordatorioService", "Notificación enviada exitosamente")
    }
}