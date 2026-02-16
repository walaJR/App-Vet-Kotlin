package com.example.veterinariaapp.data.model

enum class TipoServicio(val costoBase: Double, val displayName: String) {
    VACUNA(30000.0, "Vacuna"),
    CONSULTA_GENERAL(45000.0, "Consulta General"),
    ESTERILIZACION(150000.0, "Esterilización"),
    CIRUGIA(200000.0, "Cirugía"),
    EMERGENCIA(80000.0, "Emergencia")
}
