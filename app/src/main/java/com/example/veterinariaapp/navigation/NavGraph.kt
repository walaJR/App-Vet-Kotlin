package com.example.veterinariaapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.veterinariaapp.ui.screens.*
import com.example.veterinariaapp.ui.viewmodel.VeterinariaViewModel

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object RegistrarMascota : Screen("registrar_mascota")
    object RegistrarConsulta : Screen("registrar_consulta")
    object Listados : Screen("listados")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: VeterinariaViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToRegistrarMascota = {
                    navController.navigate(Screen.RegistrarMascota.route)
                },
                onNavigateToRegistrarConsulta = {
                    navController.navigate(Screen.RegistrarConsulta.route)
                },
                onNavigateToListados = {
                    navController.navigate(Screen.Listados.route)
                }
            )
        }

        composable(Screen.RegistrarMascota.route) {
            RegistrarMascotaScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RegistrarConsulta.route) {
            RegistrarConsultaScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Listados.route) {
            ListadoScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}