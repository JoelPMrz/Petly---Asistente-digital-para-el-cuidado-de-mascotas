package com.jdev.petly.data.models

import com.google.firebase.Timestamp
import com.jdev.petly.utils.toLocalDateTime
import com.jdev.petly.utils.toTimestamp
import java.time.LocalDateTime

data class Allergy(
    var id: String = "",
    var petId: String = "",
    var name: String = "",
    var symptoms: String? = null,
    var treatment: String? = "",
    var severity: String = "leve",
    var notes: String? = null,
    var diagnosedAt: LocalDateTime? = null,
    var createdBy: String = "",
    var editedBy: String? = null,
    var lastEditAt: LocalDateTime? = null
)

fun Allergy.toFirestoreMap(): Map<String, Any?>{
    return mapOf(
        "id" to id,
        "petId" to petId,
        "name" to name,
        "symptoms" to symptoms,
        "severity" to severity,

    )
}