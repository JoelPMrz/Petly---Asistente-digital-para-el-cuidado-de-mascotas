package com.example.petly.data.models

import com.example.petly.utils.toLocalDate
import com.example.petly.utils.toLocalTime
import com.example.petly.utils.toTimestamp
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class VeterinaryVisit(
    var id: String = "",
    var petId: String = "",
    var title: String = "",
    var description: String? = "",
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now(),
    var veterinary: String? = "",
    var createdBy: String = "",
)

fun VeterinaryVisit.toFirestoreMap(): Map<String, Any?>{
    val dateTime = LocalDateTime.of(date, time)
    return mapOf(
        "id" to id,
        "petId" to petId,
        "title" to title,
        "description" to description,
        "dateTime" to dateTime.toTimestamp(),
        "veterinary" to veterinary,
        "createdBy" to createdBy
    )
}

fun veterinaryVisitFromFirebase(map: Map<String, Any?>): VeterinaryVisit {
    val dateTime = map["dateTime"] as? Timestamp
    val date = dateTime?.toLocalDate() ?: LocalDate.now()
    val time = dateTime?.toLocalTime() ?: LocalTime.now()

    return VeterinaryVisit(
        id = map["id"] as? String ?: "",
        petId = map["petId"] as? String ?: "",
        title = map["title"] as? String ?: "",
        description = map["description"] as? String,
        date = date,
        time = time,
        veterinary = map["veterinary"] as? String,
        createdBy = map["createdBy"] as? String ?: ""
    )
}