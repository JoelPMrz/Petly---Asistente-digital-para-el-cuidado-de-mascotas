package com.jdev.petly.data.models

import com.jdev.petly.utils.toLocalDate
import com.jdev.petly.utils.toLocalTime
import com.jdev.petly.utils.toTimestamp
import com.google.firebase.Timestamp
import com.jdev.petly.utils.toLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class VeterinaryVisit(
    var id: String = "",
    var petId: String = "",
    var concept: String = "",
    var description: String? = "",
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now(),
    var veterinary: String? = "",
    var createdBy: String = "",
    var editedBy: String? = null,
    var lastEditAt: LocalDateTime? = null,
    var completed: Boolean = false
)

fun VeterinaryVisit.toFirestoreMap(): Map<String, Any?>{
    val dateTime = LocalDateTime.of(date, time)
    return mapOf(
        "id" to id,
        "petId" to petId,
        "concept" to concept,
        "description" to description,
        "dateTime" to dateTime.toTimestamp(),
        "veterinary" to veterinary,
        "createdBy" to createdBy,
        "editedBy" to createdBy,
        "lastEditAt" to lastEditAt?.toTimestamp(),
        "completed" to completed
    )
}

fun veterinaryVisitFromFirebase(map: Map<String, Any?>): VeterinaryVisit {
    val dateTime = map["dateTime"] as? Timestamp
    val lastEditAt = map["lastEditAt"] as? Timestamp
    val date = dateTime?.toLocalDate() ?: LocalDate.now()
    val time = dateTime?.toLocalTime() ?: LocalTime.now()

    return VeterinaryVisit(
        id = map["id"] as? String ?: "",
        petId = map["petId"] as? String ?: "",
        concept = map["concept"] as? String ?: "",
        description = map["description"] as? String ?: "",
        date = date,
        time = time,
        veterinary = map["veterinary"] as? String ?: "",
        createdBy = map["createdBy"] as? String ?: "",
        editedBy = map["editedBy"] as? String ?: "",
        lastEditAt = lastEditAt?.toLocalDateTime(),
        completed = map["completed"] as? Boolean ?: false
    )
}