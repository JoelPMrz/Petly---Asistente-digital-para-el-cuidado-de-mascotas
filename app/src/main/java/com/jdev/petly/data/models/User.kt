package com.jdev.petly.data.models

data class User(
    var id : String = "",
    var name: String? = null,
    var email: String,
    var photo: String? = null,
)

fun User.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "name" to name,
        "email" to email,
        "photo" to photo
    )
}


fun userfromFirestoreMap(map: Map<String, Any?>): User {
    return User(
        id = map["id"] as? String ?: "",
        name = map["name"] as? String,
        email = map["email"] as? String ?: "",
        photo = map["photo"] as? String
    )
}

