package com.example.veterinariaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
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
import com.example.veterinariaapp.ui.theme.VeterinariaAppTheme
import com.example.veterinariaapp.ui.viewmodel.RegistroViewModel
import kotlinx.coroutines.delay

class RegistroActivity : ComponentActivity() {

    private val viewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VeterinariaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistroScreen(
                        viewModel = viewModel,
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    onNavigateBack: () -> Unit
) {
    // Observar estados del ViewModel
    val nombre by viewModel.nombre.collectAsState()
    val especie by viewModel.especie.collectAsState()
    val edad by viewModel.edad.collectAsState()
    val peso by viewModel.peso.collectAsState()
    val nombreDueno by viewModel.nombreDueno.collectAsState()
    val telefonoDueno by viewModel.telefonoDueno.collectAsState()
    val emailDueno by viewModel.emailDueno.collectAsState()
    val especieExpandida by viewModel.especieExpandida.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            delay(2000)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Mascota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (registroExitoso) {
                    // Mensaje de éxito
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Mascota Registrada",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Los datos se han guardado correctamente",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    // Formulario de registro
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Datos de la Mascota",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Nombre
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { viewModel.onNombreChange(it) },
                                label = { Text("Nombre de la mascota *") },
                                leadingIcon = { Icon(Icons.Filled.Pets, null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Especie (Dropdown)
                            ExposedDropdownMenuBox(
                                expanded = especieExpandida,
                                onExpandedChange = { viewModel.onEspecieExpandidaChange(it) }
                            ) {
                                OutlinedTextField(
                                    value = especie,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Especie *") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = especieExpandida)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    enabled = !isLoading
                                )

                                ExposedDropdownMenu(
                                    expanded = especieExpandida,
                                    onDismissRequest = { viewModel.onEspecieExpandidaChange(false) }
                                ) {
                                    listOf("Perro", "Gato", "Conejo", "Hamster", "Ave", "Reptil", "Otro").forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                viewModel.onEspecieChange(option)
                                                viewModel.onEspecieExpandidaChange(false)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Edad y Peso en la misma fila
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Edad
                                OutlinedTextField(
                                    value = edad,
                                    onValueChange = { viewModel.onEdadChange(it) },
                                    label = { Text("Edad (años) *") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Peso
                                OutlinedTextField(
                                    value = peso,
                                    onValueChange = { viewModel.onPesoChange(it) },
                                    label = { Text("Peso (kg) *") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Datos del dueño
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Datos del Dueño",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = nombreDueno,
                                onValueChange = { viewModel.onNombreDuenoChange(it) },
                                label = { Text("Nombre completo *") },
                                leadingIcon = { Icon(Icons.Filled.Person, null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = telefonoDueno,
                                onValueChange = { viewModel.onTelefonoDuenoChange(it) },
                                label = { Text("Teléfono *") },
                                leadingIcon = { Icon(Icons.Filled.Phone, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = emailDueno,
                                onValueChange = { viewModel.onEmailDuenoChange(it) },
                                label = { Text("Email *") },
                                leadingIcon = { Icon(Icons.Filled.Email, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                isError = errorMessage?.contains("email", ignoreCase = true) == true
                            )
                        }
                    }

                    // Mensaje de error
                    errorMessage?.let { message ->
                        if (message.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de registro
                    Button(
                        onClick = { viewModel.registrarMascota() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Filled.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "REGISTRAR MASCOTA",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}