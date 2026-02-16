package com.example.veterinariaapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.veterinariaapp.ui.components.LoadingIndicator
import com.example.veterinariaapp.ui.components.MenuDropdown
import com.example.veterinariaapp.ui.components.VeterinariaTopBar
import com.example.veterinariaapp.ui.viewmodel.VeterinariaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: VeterinariaViewModel,
    onNavigateToRegistrarMascota: () -> Unit,
    onNavigateToRegistrarConsulta: () -> Unit,
    onNavigateToListados: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val resumenVisible by viewModel.resumenVisible.collectAsState()

    // Animación Fade In
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        topBar = {
            VeterinariaTopBar(
                onMenuClick = { menuExpanded = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingIndicator("Generando resumen...")
            } else {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(1000)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ResumenCard(viewModel)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { menuExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "ABRIR MENÚ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Menu Dropdown
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                MenuDropdown(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onRegistrarMascota = onNavigateToRegistrarMascota,
                    onRegistrarConsulta = onNavigateToRegistrarConsulta,
                    onVerListados = onNavigateToListados
                )
            }
        }
    }
}

@Composable
fun ResumenCard(viewModel: VeterinariaViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "RESUMEN DEL SISTEMA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            ResumenItem("🐾 Total de Mascotas", viewModel.getTotalMascotas().toString())
            Spacer(modifier = Modifier.height(12.dp))

            ResumenItem("🏥 Total de Consultas", viewModel.getTotalConsultas().toString())
            Spacer(modifier = Modifier.height(12.dp))

            ResumenItem("👤 Último Dueño Registrado", viewModel.getUltimoDueno())
        }
    }
}

@Composable
fun ResumenItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}