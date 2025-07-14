package com.jdev.petly.data.models

import com.google.firebase.Timestamp
import com.jdev.petly.utils.toLocalDateTime
import com.jdev.petly.utils.toTimestamp
import java.time.LocalDateTime

data class Vaccine(
    var id: String = "",
    var petId: String = "",
    var name: String = "",
    var note: String? = "",
    var doses: List<String> = listOf(),
    var createdBy: String = "",
    var editedBy: String? = null,
    var lastEditAt: LocalDateTime? = null,
)

fun Vaccine.toFirestoreMap(): Map<String, Any?>{
    return mapOf(
        "id" to id,
        "petId" to petId,
        "name" to name,
        "note" to note,
        "doses" to doses,
        "createdBy" to createdBy,
        "editedBy" to editedBy,
        "lastEditAt" to lastEditAt?.toTimestamp()
    )
}

fun vaccineFromFirebase(map: Map<String, Any?>): Vaccine {
    val lastEditAt = map["lastEditAt"] as? Timestamp

    return Vaccine (
        id = map["id"] as? String ?: "",
        petId = map["petId"] as? String ?: "",
        name = map["name"] as? String ?: "",
        note = map["note"] as? String ?: "",
        doses = map["doses"] as? List<String> ?: listOf(),
        createdBy = map["createdBy"] as? String ?: "",
        editedBy = map["editedBy"] as? String ?: "",
        lastEditAt = lastEditAt?.toLocalDateTime()
    )
}