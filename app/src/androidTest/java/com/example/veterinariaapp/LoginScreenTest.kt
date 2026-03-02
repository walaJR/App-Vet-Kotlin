package com.example.veterinariaapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginFlow_camposVacios_muestraError() {
        composeTestRule.setContent {
            LoginScreen(onLoginSuccess = {}, onRecuperarContrasena = {})
        }
        composeTestRule.onNodeWithText("INICIAR SESIÓN").performClick()
        composeTestRule.onNodeWithText("Por favor, completa todos los campos").assertIsDisplayed()
    }

    @Test
    fun loginFlow_ingresoDeCredenciales_activaLoading() {
        composeTestRule.setContent {
            LoginScreen(onLoginSuccess = {}, onRecuperarContrasena = {})
        }
        composeTestRule.onNodeWithText("Usuario o Email").performTextInput("admin@vet.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("123456")
        composeTestRule.onNodeWithText("INICIAR SESIÓN").performClick()
        composeTestRule.onNodeWithText("INICIAR SESIÓN").assertDoesNotExist()
    }
}