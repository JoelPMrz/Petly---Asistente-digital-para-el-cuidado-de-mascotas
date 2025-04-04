package com.example.petly.data.models

data class PetModel (
    val key: String? = null,
    val name: String,                // Nombre de la mascota
    val type: String,                // Tipo (Perro, Gato, etc.)
    val breed: String?,              // Raza (opcional)
    val weight: Float?,              // Peso (opcional)
    val gender: String,              // Género (masculino/femenino)
    val birthDate: String?,          // Fecha de nacimiento (opcional)
    val photo: Int?,              // URL de la foto (opcional)
    val ownerId: String,             // ID del dueño
    //val vaccines: List<Vaccines>?,     // Lista de vacunas (opcional)
    val microchipId: String?,        // ID del microchip (opcional)
    val adoptionDate: String?
) {
    constructor() : this(null, "", "", null, null, "", null, null, "", null, null)
}