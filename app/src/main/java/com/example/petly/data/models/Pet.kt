package com.example.petly.data.models

import java.time.LocalDate

data class Pet(
    var id: String? = "",
    var name: String = "",
    var type: String = "",
    var breed: String? = null,
    var weights: List<String>? = listOf(),
    var gender: String = "",
    var birthDate: LocalDate? = null,
    var photo: Int? = null,
    var owners: List<String> = listOf(),
    var observers: List<String> = listOf(), // IDs de los ojeadores
    var vaccines: List<String>? = listOf(), // Lista de IDs de vacunas
    var reminders: List<String>? = listOf(), // Lista de IDs de recordatorios
    var microchipId: String? = null,
    var adoptionDate: LocalDate? = null,
    var sterilized: Boolean? = null
)

