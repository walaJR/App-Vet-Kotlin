package com.example.veterinariaapp.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: RegistroViewModel
    private val applicationMock: Application = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegistroViewModel(applicationMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registrarMascota con datos invalidos actualiza errorMessage`() = runTest {
        viewModel.onNombreChange("")
        viewModel.registrarMascota()
        testDispatcher.scheduler.advanceUntilIdle()
        val errorActual = viewModel.errorMessage.value
        assert(errorActual?.contains("Error de validación") == true)
    }

    @Test
    fun `actualizar campos de formulario refleja el cambio en los StateFlow`() {
        viewModel.onNombreChange("Firulais")
        viewModel.onEspecieChange("Perro")
        Assert.assertEquals("Firulais", viewModel.nombre.value)
        Assert.assertEquals("Perro", viewModel.especie.value)
    }
}