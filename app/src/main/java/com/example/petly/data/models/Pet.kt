package com.example.petly.data.models

import com.example.petly.utils.toLocalDate
import com.example.petly.utils.toTimestamp
import java.time.LocalDate
import com.google.firebase.Timestamp
import java.time.Period

data class Pet(
    var id: String? = "",
    var name: String = "",
    var type: String = "",
    var breed: String? = null,
    var weights: List<String>? = listOf(),
    var gender: String = "",
    var birthDate: LocalDate? = null,
    var photo: String? = null,
    var owners: List<String> = listOf(),
    var observers: List<String> = listOf(),
    var vaccines: List<String>? = listOf(),
    var reminders: List<String>? = listOf(),
    var allergies: List<String>? = listOf(),
    var medicalConditions: List<String>? = listOf(),
    var microchipId: String? = null,
    var microchipDate: LocalDate? = null,
    var passportId: String? = null,
    var adoptionDate: LocalDate? = null,
    var sterilized: Boolean? = false,
    var sterilizedDate: LocalDate? = null,
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
        "sterilized" to sterilized,
        "microchipDate" to microchipDate?.toTimestamp(),
        "sterilizedDate" to sterilizedDate?.toTimestamp(),
    )
}

fun fromFirestoreMap(map: Map<String, Any?>): Pet {
    val birthTimestamp = map["birthDate"] as? Timestamp
    val adoptionTimestamp = map["adoptionDate"] as? Timestamp
    val microchipTimestamp = map["microchipDate"] as? Timestamp
    val sterilizedTimestamp = map["sterilizedDate"] as? Timestamp

    return Pet(
        id = map["id"] as? String,
        name = map["name"] as? String ?: "",
        type = map["type"] as? String ?: "",
        breed = map["breed"] as? String,
        weights = map["weights"] as? List<String> ?: listOf(),
        gender = map["gender"] as? String ?: "",
        birthDate = birthTimestamp?.toLocalDate(),
        photo = (map["photo"] as? String)?: "",
        owners = map["owners"] as? List<String> ?: listOf(),
        observers = map["observers"] as? List<String> ?: listOf(),
        vaccines = map["vaccines"] as? List<String> ?: listOf(),
        reminders = map["reminders"] as? List<String> ?: listOf(),
        microchipId = map["microchipId"] as? String,
        microchipDate = microchipTimestamp?.toLocalDate(),
        adoptionDate = adoptionTimestamp?.toLocalDate(),
        sterilized = map["sterilized"] as? Boolean,
        sterilizedDate = sterilizedTimestamp?.toLocalDate()
    )
}

fun Pet.getAge(): String {
    val birthDate = this.birthDate ?: return "Fecha desconocida"
    val today = LocalDate.now()

    val period = Period.between(birthDate, today)

    val years = period.years
    val months = period.months
    val days = period.days

    val parts = mutableListOf<String>()
    if (years > 0) parts.add("$years ${if (years == 1) "año" else "años"}")
    if (months > 0) parts.add("$months ${if (months == 1) "mes" else "meses"}")
    if (days > 0 || parts.isEmpty()) parts.add("$days ${if (days == 1) "día" else "días"}")

    return parts.joinToString(", ")
}
