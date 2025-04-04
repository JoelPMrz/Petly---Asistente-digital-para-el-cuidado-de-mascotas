package com.example.petly.data.models

data class Pet (
    var id: String? = null,
    var name: String,                // Nombre de la mascota
    var type: String,                // Tipo (Perro, Gato, etc.)
    var breed: String?,              // Raza (opcional)
    var weight: Float?,              // Peso (opcional)
    var gender: String,              // Género (masculino/femenino)
    var birthDate: String?,          // Fecha de nacimiento (opcional)
    var photo: Int?,              // URL de la foto (opcional)
    var ownerId: String,             // ID del dueño
    //val vaccines: List<Vaccines>?,     // Lista de vacunas (opcional)
    var microchipId: String?,        // ID del microchip (opcional)
    var adoptionDate: String?
) {
    constructor() : this(null, "", "", null, null, "", null, null, "", null, null)
}