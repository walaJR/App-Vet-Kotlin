package com.example.veterinariaapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.veterinariaapp.service.RecordatorioService
import com.example.veterinariaapp.ui.theme.VeterinariaAppTheme
import com.example.veterinariaapp.ui.viewmodel.MainViewModel
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            iniciarServicio()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        solicitarPermisoNotificaciones()

        setContent {
            VeterinariaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel = viewModel()
                    MainScreen(
                        viewModel = viewModel,
                        onNavigateToRegistro = {
                            startActivity(Intent(this, RegistroActivity::class.java))
                        },
                        onNavigateToConsulta = {
                            startActivity(Intent(this, ConsultaActivity::class.java))
                        },
                        onNavigateToListado = {
                            startActivity(Intent(this, ListadoActivityV4::class.java))
                        },
                        onNavigateToListadoV4 = {
                            startActivity(Intent(this, ListadoActivityV4::class.java))
                        }
                    )
                }
            }
        }
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    iniciarServicio()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            iniciarServicio()
        }
    }

    private fun iniciarServicio() {
        try {
            Intent(this, RecordatorioService::class.java).also { intent ->
                startService(intent)
            }
        } catch (e: Exception) {
            // Ignorar si el servicio falla (no es crítico)
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToRegistro: () -> Unit,
    onNavigateToConsulta: () -> Unit,
    onNavigateToListado: () -> Unit,
    onNavigateToListadoV4: () -> Unit
) {

    val context = LocalContext.current

    val totalMascotas by viewModel.totalMascotas.collectAsState()
    val totalConsultas by viewModel.totalConsultas.collectAsState()
    val ultimoDueno by viewModel.ultimoDueno.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarResumen()
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Logo Veterinaria",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Sistema Veterinaria",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Gestión integral de mascotas y consultas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card de resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "Resumen del Sistema",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    ResumenItem("Total Mascotas", totalMascotas.toString())
                    Spacer(modifier = Modifier.height(12.dp))
                    ResumenItem("Total Consultas", totalConsultas.toString())
                    Spacer(modifier = Modifier.height(12.dp))
                    ResumenItem("Último Dueño", ultimoDueno)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de navegación
            Button(
                onClick = onNavigateToRegistro,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar Mascota", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNavigateToConsulta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gestionar Consultas", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNavigateToListadoV4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver Listados", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            //  BOTÓN TEMPORAL SOLO PARA TESTING
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, TestImageActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("TEST: Ver Imagen con Glide")
            }
        }
    }
}

@Composable
fun ResumenItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}