package com.example.veterinariaapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.veterinariaapp.data.model.Mascota
import com.example.veterinariaapp.data.model.Consulta
import com.example.veterinariaapp.ui.viewmodel.VeterinariaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(
    viewModel: VeterinariaViewModel,
    onNavigateBack: () -> Unit
) {
    val mascotas by viewModel.mascotas.collectAsState()
    val consultas by viewModel.consultas.collectAsState()

    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Mascotas", "Consultas")

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                when (tabIndex) {
                    0 -> ListadoMascotas(mascotas)
                    1 -> ListadoConsultas(consultas)
                }
            }
        }
    }
}

@Composable
fun ListadoMascotas(mascotas: List<Mascota>) {
    if (mascotas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("No hay mascotas registradas")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mascotas) { mascota ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            mascota.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Especie: ${mascota.especie}")
                        Text("Edad: ${mascota.edad} años")
                        Text("Peso: ${mascota.peso} kg")
                        Text("Dueño: ${mascota.dueno.nombre}")
                    }
                }
            }
        }
    }
}

@Composable
fun ListadoConsultas(consultas: List<Consulta>) {
    if (consultas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("No hay consultas registradas")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(consultas) { consulta ->
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            consulta.tipoServicio.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Mascota: ${consulta.mascota.nombre}")
                        Text("Veterinario: ${consulta.veterinario.nombre}")
                        Text("Costo: $${String.format("%.0f", consulta.costoTotal)}")
                        if (consulta.descuentoAplicado) {
                            Text(
                                "Descuento aplicado (15%)",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text("Estado: ${consulta.estado}")
                        Text("Fecha: ${consulta.formatearFecha()}")
                    }
                }
            }
        }
    }
}