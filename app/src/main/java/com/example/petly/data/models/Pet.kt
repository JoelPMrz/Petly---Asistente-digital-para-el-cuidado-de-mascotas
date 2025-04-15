package com.example.petly.data.models

import com.example.petly.utils.toLocalDate
import com.example.petly.utils.toTimestamp
import java.time.LocalDate
import com.google.firebase.Timestamp

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
    var allergies: List<String>? = listOf(),
    var medicalConditions: List<String>? = listOf(),
    var microchipId: String? = null,
    var passportId: String? = null,
    var adoptionDate: LocalDate? = null,
    var sterilized: Boolean? = null
)

fun Pet.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "name" to name,
        "type" to type,
        "breed" to breed,
        "weights" to weights,
        "gender" to gender,
        "birthDate" to birthDate?.toTimestamp(),
        "photo" to photo,
        "owners" to owners,
        "observers" to observers,
        "vaccines" to vaccines,
        "reminders" to reminders,
        "microchipId" to microchipId,
        "adoptionDate" to adoptionDate?.toTimestamp(),
        "sterilized" to sterilized
    )
}

fun fromFirestoreMap(map: Map<String, Any?>): Pet {
    val birthTimestamp = map["birthDate"] as? Timestamp
    val adoptionTimestamp = map["adoptionDate"] as? Timestamp

    return Pet(
        id = map["id"] as? String,
        name = map["name"] as? String ?: "",
        type = map["type"] as? String ?: "",
        breed = map["breed"] as? String,
        weights = map["weights"] as? List<String> ?: listOf(),
        gender = map["gender"] as? String ?: "",
        birthDate = birthTimestamp?.toLocalDate(),
        photo = (map["photo"] as? Long)?.toInt(), // Firebase guarda Int como Long
        owners = map["owners"] as? List<String> ?: listOf(),
        observers = map["observers"] as? List<String> ?: listOf(),
        vaccines = map["vaccines"] as? List<String>,
        reminders = map["reminders"] as? List<String>,
        microchipId = map["microchipId"] as? String,
        adoptionDate = adoptionTimestamp?.toLocalDate(),
        sterilized = map["sterilized"] as? Boolean
    )
}

