package com.jdev.petly.data.models

import com.google.firebase.Timestamp
import com.jdev.petly.utils.toLocalDate
import com.jdev.petly.utils.toLocalDateTime
import com.jdev.petly.utils.toLocalTime
import com.jdev.petly.utils.toTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Dose(
    var id: String = "",
    var vaccineId: String = "",
    var petId: String = "",
    var note: String? = "",
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now(),
    var applied: Boolean = false,
    var createdBy: String = "",
    var editedBy: String? = null,
    var lastEditAt: LocalDateTime? = null,
)

fun Dose.toFirebasestore(): Map<String, Any?> {
    val dateTime = LocalDateTime.of(date, time)
    return mapOf(
        "id" to id,
        "vaccineId" to vaccineId,
        "petId" to petId,
        "note" to note,
        "dateTime" to dateTime.toTimestamp(),
        "applied" to applied,
        "createdBy" to createdBy,
        "editedBy" to editedBy,
        "lastEditAt" to lastEditAt?.toTimestamp(),
    )
}

fun doseFromFirebase(map: Map<String,Any?>) : Dose {
    val dateTime = map["dateTime"] as? Timestamp
    val date = dateTime?.toLocalDate() ?: LocalDate.now()
    val time = dateTime?.toLocalTime() ?: LocalTime.now()
    val lastEditAt = map["lastEditAt"] as? Timestamp
    return Dose(
        id = map["id"] as? String ?: "",
        vaccineId = map["vaccineId"] as? String ?: "",
        petId = map["petId"] as? String ?: "",
        note = map["note"] as? String ?: "",
        date = date,
        time = time,
        createdBy = map["createdBy"] as? String ?: "",
        editedBy = map["editedBy"] as? String ?: "",
        lastEditAt = lastEditAt?.toLocalDateTime(),
        applied = map["applied"] as? Boolean ?: false
    )
}
