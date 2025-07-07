package com.example.registrohorasapp.models

data class RegistroRequest(
    val fecha: String,
    val horas: Float
)

data class RegistroResponse(
    val id: Int,
    val fecha: String,
    val horas: Float
)

data class ResumenResponse(
    val inicio_semana: String,
    val fin_semana: String,
    val total_horas: Float,
    val horas_extra: Float
)
