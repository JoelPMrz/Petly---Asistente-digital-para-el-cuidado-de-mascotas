package com.jdev.petly.data.models

import com.jdev.petly.utils.toLocalDate
import com.jdev.petly.utils.toLocalDateTime
import com.jdev.petly.utils.toTimestamp
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

data class Weight(
    var id: String? = null,
    var petId: String? = null,
    var value: Double = 0.0,
    var unit: String = "Kg",
    var date: LocalDate = LocalDate.now(),
    var time: LocalDateTime = LocalDateTime.now(),
    var createdBy: String = "",
    var editedBy: String? = null,
    var lastEditAt: LocalDateTime? = null,
    var notes: String? = null
){
    override fun toString(): String {
        return "id: $id Peso: $value"
    }
}

fun Weight.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "petId" to petId,
        "value" to value,
        "unit" to unit,
        "date" to date.toTimestamp(),
        "time" to time.toTimestamp(),
        "createdBy" to createdBy,
        "editedBy" to editedBy,
        "lastEditAt" to lastEditAt?.toTimestamp(),
        "notes" to notes
    )
}


fun weightFromFirestoreMap(map: Map<String, Any?>): Weight {
    val dateTimestamp = map["date"] as? Timestamp
    val timeTimestamp = map["time"] as? Timestamp
    val lastEditAt = map["lastEditAt"] as? Timestamp
    return Weight(
        id = map["id"] as? String,
        petId = map["petId"] as? String,
        value = (map["value"] as? Number)?.toDouble() ?: 0.0,
        unit = map["unit"] as? String ?: "Kg",
        date = dateTimestamp?.toLocalDate() ?: LocalDate.now(),
        time = timeTimestamp?.toLocalDateTime() ?: LocalDateTime.now(),
        createdBy = map["createdBy"] as? String ?: "",
        editedBy = map["editedBy"] as? String ?: "",
        lastEditAt = lastEditAt?.toLocalDateTime(),
        notes = map["notes"] as? String
    )
}



