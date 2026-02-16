package com.example.veterinariaapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.veterinariaapp.data.model.*
import com.example.veterinariaapp.ui.theme.VeterinariaAppTheme
import com.example.veterinariaapp.ui.viewmodel.ConsultaViewModel

class ConsultaActivity : ComponentActivity() {

    private val viewModel: ConsultaViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ConsultaViewModel(application) as T
            }
        }
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                VeterinariaAppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ConsultaScreen(
                            viewModel = viewModel,
                            onNavigateBack = { finish() },
                            onCompartirConsulta = { consulta ->
                                compartirConsulta(consulta)
                            }
                        )
                    }
                }
            }
        }

        // Intent Implícito para compartir
        private fun compartirConsulta(consulta: Consulta) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, consulta.toShareText())
                putExtra(Intent.EXTRA_SUBJECT, "Consulta Veterinaria - ${consulta.mascota.nombre}")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir consulta mediante..."))
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaScreen(
    viewModel: ConsultaViewModel,
    onNavigateBack: () -> Unit,
    onCompartirConsulta: (Consulta) -> Unit
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Registrar", "Listado")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Consultas") },
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

            when (tabIndex) {
                0 -> RegistrarConsultaTab(viewModel)
                1 -> ListadoConsultasTab(viewModel, onCompartirConsulta)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarConsultaTab(viewModel: ConsultaViewModel) {
    val mascotas by viewModel.mascotas.collectAsState()
    val veterinarios by viewModel.veterinarios.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val consultaGuardada by viewModel.consultaGuardada.collectAsState()

    var mascotaSeleccionada by remember { mutableStateOf<Mascota?>(null) }
    var veterinarioSeleccionado by remember { mutableStateOf<Veterinario?>(null) }
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

    LaunchedEffect(consultaGuardada) {
        if (consultaGuardada) {
            kotlinx.coroutines.delay(2000)
            viewModel.resetearEstado()
            // Limpiamos el formulario
            mascotaSeleccionada = null
            veterinarioSeleccionado = null
            tipoServicioSeleccionado = null
            descripcion = ""
            tiempoMinutos = ""
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800)) + slideInVertically(),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        if (mascotas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay mascotas registradas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Registra una mascota primero",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (consultaGuardada) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Consulta registrada exitosamente",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    "Nueva Consulta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Seleccionar Mascota
                ExposedDropdownMenuBox(
                    expanded = expandedMascota,
                    onExpandedChange = { expandedMascota = !expandedMascota }
                ) {
                    OutlinedTextField(
                        value = mascotaSeleccionada?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Mascota") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMascota) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )

                    ExposedDropdownMenu(
                        expanded = expandedMascota,
                        onDismissRequest = { expandedMascota = false }
                    ) {
                        mascotas.forEach { mascota ->
                            DropdownMenuItem(
                                text = { Text("${mascota.nombre} (${mascota.especie}) - ${mascota.dueno.nombre}") },
                                onClick = {
                                    mascotaSeleccionada = mascota
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
                        value = veterinarioSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Veterinario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVet) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )

                    ExposedDropdownMenu(
                        expanded = expandedVet,
                        onDismissRequest = { expandedVet = false }
                    ) {
                        veterinarios.forEach { vet ->
                            DropdownMenuItem(
                                text = { Text("${vet.nombre} - ${vet.especialidad}") },
                                onClick = {
                                    veterinarioSeleccionado = vet
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
                            .menuAnchor(),
                        enabled = !isLoading
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
                    label = { Text("Descripción / Motivo") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tiempoMinutos,
                    onValueChange = { tiempoMinutos = it },
                    label = { Text("Tiempo estimado (minutos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN TEMPORAL SOLO PARA TESTING
                OutlinedButton(
                    onClick = {
                        viewModel.simularError("validation")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.BugReport, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TEST: Simular Error")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        mascotaSeleccionada?.let { mascota ->
                            veterinarioSeleccionado?.let { vet ->
                                tipoServicioSeleccionado?.let { servicio ->
                                    if (descripcion.isNotEmpty() && tiempoMinutos.isNotEmpty()) {
                                        viewModel.registrarConsulta(
                                            mascota,
                                            vet,
                                            servicio,
                                            descripcion,
                                            tiempoMinutos.toInt()
                                        )
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading && mascotaSeleccionada != null &&
                            veterinarioSeleccionado != null && tipoServicioSeleccionado != null &&
                            descripcion.isNotEmpty() && tiempoMinutos.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("REGISTRAR CONSULTA")
                    }
                }
            }
        }
    }
}



@Composable
fun ListadoConsultasTab(
    viewModel: ConsultaViewModel,
    onCompartirConsulta: (Consulta) -> Unit
) {
    val consultas by viewModel.consultas.collectAsState()

    var showDialogEstado by remember { mutableStateOf<Consulta?>(null) }
    var showDialogEliminar by remember { mutableStateOf<Consulta?>(null) }

    if (consultas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No hay consultas registradas")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(consultas) { consulta ->
                ConsultaCard(
                    consulta = consulta,
                    onCambiarEstado = { showDialogEstado = consulta },
                    onCompartir = { onCompartirConsulta(consulta) },
                    onEliminar = { showDialogEliminar = consulta }
                )
            }
        }
    }

    // Dialogo para cambiar estado
    showDialogEstado?.let { consulta ->
        AlertDialog(
            onDismissRequest = { showDialogEstado = null },
            title = { Text("Cambiar Estado") },
            text = {
                Column {
                    EstadoConsulta.values().forEach { estado ->
                        TextButton(
                            onClick = {
                                viewModel.actualizarEstadoConsulta(consulta, estado)
                                showDialogEstado = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(estado.name)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialogEstado = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialogo para confirmar eliminación
    showDialogEliminar?.let { consulta ->
        AlertDialog(
            onDismissRequest = { showDialogEliminar = null },
            title = { Text("Eliminar Consulta") },
            text = { Text("¿Está seguro de eliminar esta consulta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarConsulta(consulta.id)
                        showDialogEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ConsultaCard(
    consulta: Consulta,
    onCambiarEstado: () -> Unit,
    onCompartir: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    consulta.tipoServicio.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                AssistChip(
                    onClick = onCambiarEstado,
                    label = { Text(consulta.estado.name) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("🐾 Mascota: ${consulta.mascota.nombre}")
            Text("👤 Dueño: ${consulta.mascota.dueno.nombre}")
            Text("👨‍⚕️ Veterinario: ${consulta.veterinario.nombre}")
            Text("📝 ${consulta.descripcion}")
            Text("💰 Costo: $${String.format("%.0f", consulta.costoTotal)}")

            if (consulta.descuentoAplicado) {
                Text(
                    "Descuento aplicado (15%)",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Text("📅 ${consulta.formatearFecha()}")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCompartir,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compartir")
                }

                OutlinedButton(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}