package com.example.veterinariaapp.utils

import org.junit.Assert
import org.junit.Test

class ValidacionesTest {

    @Test
    fun validarEmail_conEmailCorrecto_retornaTrue() {
        val emailValido = "usuario@veterinaria.com"
        Assert.assertTrue("El email debería ser válido", Validaciones.validarEmail(emailValido))
    }

    @Test
    fun validarEmail_conEmailSinArroba_retornaFalse() {
        val emailInvalido = "usuarioveterinaria.com"
        Assert.assertFalse(
            "El email no debería ser válido",
            Validaciones.validarEmail(emailInvalido)
        )
    }

    @Test
    fun validarTelefono_conFormatoCorrecto_retornaTrue() {
        val telefonoValido = "+56912345678"
        Assert.assertTrue(
            "El teléfono debería ser válido",
            Validaciones.validarTelefono(telefonoValido)
        )
    }

    @Test
    fun formatearPrecio_conNumeroValido_retornaFormatoMoneda() {
        val precio = 150000.0
        val resultado = Validaciones.formatearPrecio(precio)
        // Dependiendo del Locale podría incluir punto o coma, verificamos la estructura básica
        Assert.assertTrue("Debe empezar con el signo $", resultado.startsWith("$"))
        Assert.assertTrue("Debe contener los dígitos correctos", resultado.contains("150"))
    }
}