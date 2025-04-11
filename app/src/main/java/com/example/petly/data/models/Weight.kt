package com.example.petly.data.models

import com.example.petly.utils.toLocalDate
import com.example.petly.utils.toTimestamp
import com.google.firebase.Timestamp
import java.time.LocalDate

data class Weight(
    var id: String? = null,
    var petId: String? = null,
    var value: Double = 0.0,
    var unit: WeightUnit = WeightUnit.KILOGRAMS,
    var date: LocalDate = LocalDate.now(),
    var notes: String? = null
){
    override fun toString(): String {
        return "id: $id Peso: $value"
    }
}

enum class WeightUnit(val displayName: String){
    KILOGRAMS("kg"),
    POUNDS("lb"),
    OUNCES("oz")
}

fun Weight.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "petId" to petId,
        "value" to value,
        "unit" to unit.name,
        "date" to date?.toTimestamp(),
        "notes" to notes,
    )
}

fun fromFirestoreMap(map: Map<String, Any?>): Weight {
    val timestamp = map["date"] as? Timestamp
    val unitStr = map["unit"] as? String

    return Weight(
        id = map["id"] as? String,
        petId = map["petId"] as? String,
        value = (map["value"] as? Number)?.toDouble() ?: 0.0,
        unit = try {
            WeightUnit.valueOf(unitStr ?: "KILOGRAMS")
        } catch (e: IllegalArgumentException) {
            WeightUnit.KILOGRAMS
        },
        notes = map["notes"] as? String,
        date = timestamp?.toLocalDate() ?: LocalDate.now()
    )
}

