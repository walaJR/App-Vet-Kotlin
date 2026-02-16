package com.example.veterinariaapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.veterinariaapp.data.model.TipoServicio
import com.example.veterinariaapp.ui.viewmodel.VeterinariaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarConsultaScreen(
    viewModel: VeterinariaViewModel,
    onNavigateBack: () -> Unit
) {
    val mascotas by viewModel.mascotas.collectAsState()
    val veterinarios by viewModel.veterinarios.collectAsState()

    var mascotaSeleccionada by remember { mutableStateOf<Int?>(null) }
    var veterinarioSeleccionado by remember { mutableStateOf<Int?>(null) }
    var tipoServicioSeleccionado by remember { mutableStateOf<TipoServicio?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var tiempoMinutos by remember { mutableStateOf("") }

    var expandedMascota by remember { mutableStateOf(false) }
    var expandedVet by remember { mutableStateOf(false) }
    var expandedServicio by remember { mutableStateOf(false) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Consulta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            if (mascotas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("No hay mascotas registradas. Registra una mascota primero.")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Seleccionar Mascota
                    ExposedDropdownMenuBox(
                        expanded = expandedMascota,
                        onExpandedChange = { expandedMascota = !expandedMascota }
                    ) {
                        OutlinedTextField(
                            value = mascotaSeleccionada?.let { mascotas[it].nombre } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Mascota") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMascota) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedMascota,
                            onDismissRequest = { expandedMascota = false }
                        ) {
                            mascotas.forEachIndexed { index, mascota ->
                                DropdownMenuItem(
                                    text = { Text(mascota.toString()) },
                                    onClick = {
                                        mascotaSeleccionada = index
                                        expandedMascota = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Seleccionar Veterinario
                    ExposedDropdownMenuBox(
                        expanded = expandedVet,
                        onExpandedChange = { expandedVet = !expandedVet }
                    ) {
                        OutlinedTextField(
                            value = veterinarioSeleccionado?.let { veterinarios[it].nombre } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Veterinario") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVet) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedVet,
                            onDismissRequest = { expandedVet = false }
                        ) {
                            veterinarios.forEachIndexed { index, vet ->
                                DropdownMenuItem(
                                    text = { Text(vet.toString()) },
                                    onClick = {
                                        veterinarioSeleccionado = index
                                        expandedVet = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tipo de Servicio
                    ExposedDropdownMenuBox(
                        expanded = expandedServicio,
                        onExpandedChange = { expandedServicio = !expandedServicio }
                    ) {
                        OutlinedTextField(
                            value = tipoServicioSeleccionado?.displayName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Servicio") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicio) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedServicio,
                            onDismissRequest = { expandedServicio = false }
                        ) {
                            TipoServicio.values().forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text("${tipo.displayName} - $${tipo.costoBase.toInt()}") },
                                    onClick = {
                                        tipoServicioSeleccionado = tipo
                                        expandedServicio = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tiempoMinutos,
                        onValueChange = { tiempoMinutos = it },
                        label = { Text("Tiempo estimado (minutos)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            mascotaSeleccionada?.let { idxMascota ->
                                veterinarioSeleccionado?.let { idxVet ->
                                    tipoServicioSeleccionado?.let { servicio ->
                                        if (descripcion.isNotEmpty() && tiempoMinutos.isNotEmpty()) {
                                            viewModel.agregarConsulta(
                                                mascotas[idxMascota],
                                                veterinarios[idxVet],
                                                servicio,
                                                descripcion,
                                                tiempoMinutos.toInt()
                                            )
                                            onNavigateBack()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = mascotaSeleccionada != null && veterinarioSeleccionado != null &&
                                tipoServicioSeleccionado != null && descripcion.isNotEmpty() &&
                                tiempoMinutos.isNotEmpty()
                    ) {
                        Text("REGISTRAR CONSULTA")
                    }
                }
            }
        }
    }
}