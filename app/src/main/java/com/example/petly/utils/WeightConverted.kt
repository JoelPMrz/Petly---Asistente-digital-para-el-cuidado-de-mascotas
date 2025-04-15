package com.example.petly.utils

import com.example.petly.data.models.Weight
import com.google.firebase.Timestamp
import java.time.LocalDate
import kotlin.math.pow


fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double {
    val converted = when (fromUnit to toUnit) {
        "Kg" to "Lb" -> value * 2.20462
        "Kg" to "Oz" -> value * 35.274
        "Kg" to "Gr" -> value * 1000.0

        "Lb" to "Kg" -> value / 2.20462
        "Lb" to "Oz" -> value * 16.0
        "Lb" to "Gr" -> (value / 2.20462) * 1000.0

        "Oz" to "Kg" -> value / 35.274
        "Oz" to "Lb" -> value / 16.0
        "Oz" to "Gr" -> (value / 35.274) * 1000.0

        "Gr" to "Kg" -> value / 1000.0
        "Gr" to "Lb" -> (value / 1000.0) * 2.20462
        "Gr" to "Oz" -> (value / 1000.0) * 35.274

        else -> value
    }
    return converted
}


fun Double.truncate(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return kotlin.math.floor(this * factor) / factor
}

fun Weight.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "petId" to petId,
        "value" to value,
        "unit" to unit,
        "date" to date.toTimestamp(),
        "notes" to notes,
    )
}

fun fromFirestoreMap(map: Map<String, Any?>): Weight {
    val timestamp = map["date"] as? Timestamp

    return Weight(
        id = map["id"] as? String,
        petId = map["petId"] as? String,
        value = (map["value"] as? Number)?.toDouble() ?: 0.0,
        unit = map["unit"] as? String ?: "Kg",
        notes = map["notes"] as? String,
        date = timestamp?.toLocalDate() ?: LocalDate.now()
    )
}

