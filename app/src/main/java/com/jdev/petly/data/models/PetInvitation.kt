package com.jdev.petly.data.models

import com.jdev.petly.utils.toLocalDateTime
import com.jdev.petly.utils.toTimestamp
import com.google.firebase.Timestamp
import java.time.LocalDateTime

data class PetInvitation(
    var id: String = "",
    val petId: String,
    val petName: String,
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val role: String,
    val accepted: Boolean = false,
    val setAt: LocalDateTime = LocalDateTime.now()
)

fun PetInvitation.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "petId" to petId,
        "petName" to petName,
        "fromUserId" to fromUserId,
        "fromUserName" to fromUserName,
        "toUserId" to toUserId,
        "role" to role,
        "accepted" to accepted,
        "setAt" to setAt.toTimestamp()
    )
}

fun petPetInvitationFromFirestoreMap(map: Map<String, Any?>): PetInvitation {
    val setAtTimestamp = map["setAt"] as? Timestamp

    return PetInvitation(
        id = map["id"] as? String ?: "",
        petId = map["petId"] as? String ?: "",
        petName = map["petName"] as? String ?: "",
        fromUserId = map["fromUserId"] as? String ?: "",
        fromUserName = map["fromUserName"] as? String ?: "",
        toUserId = map["toUserId"] as? String ?: "",
        role = map["role"] as? String ?: "",
        accepted = map["accepted"] as? Boolean ?: false,
        setAt = setAtTimestamp?.toLocalDateTime() ?: LocalDateTime.now()
    )
}