package com.example.veterinariaapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Preparación de Retrofit para API REST
 */

// Modelos para respuestas API
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

data class MascotaDto(
    val id: Long,
    val nombre: String,
    val especie: String,
    val edad: Int
)

// Interface del servicio API
interface VeterinariaApiService {

    @GET("mascotas")
    suspend fun obtenerMascotas(): ApiResponse<List<MascotaDto>>

    @POST("mascotas")
    suspend fun crearMascota(@Body mascota: MascotaDto): ApiResponse<MascotaDto>
}

/**
 * Cliente Retrofit configurado
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.veterinaria-demo.com/" // URL placeholder

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: VeterinariaApiService = retrofit.create(VeterinariaApiService::class.java)
}