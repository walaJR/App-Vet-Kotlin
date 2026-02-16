package com.example.veterinariaapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MenuDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRegistrarMascota: () -> Unit,
    onRegistrarConsulta: () -> Unit,
    onVerListados: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        MenuItemWithIcon(
            text = "Registrar Mascota",
            icon = Icons.Filled.Pets,
            onClick = {
                onRegistrarMascota()
                onDismiss()
            }
        )

        MenuItemWithIcon(
            text = "Registrar Consulta",
            icon = Icons.Filled.MedicalServices,
            onClick = {
                onRegistrarConsulta()
                onDismiss()
            }
        )

        MenuItemWithIcon(
            text = "Ver Listados",
            icon = Icons.Filled.List,
            onClick = {
                onVerListados()
                onDismiss()
            }
        )
    }
}

@Composable
fun MenuItemWithIcon(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = onClick,
        leadingIcon = {
            Icon(icon, contentDescription = text)
        }
    )
}
