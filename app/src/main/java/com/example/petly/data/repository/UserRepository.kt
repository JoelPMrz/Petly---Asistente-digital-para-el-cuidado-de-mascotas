package com.example.petly.data.repository

import com.example.petly.data.models.User
import com.example.petly.data.models.UserfromFirestoreMap
import com.example.petly.data.models.toFirestoreMap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun addUser(
        name: String?,
        email: String,
        photo: String?,
        alreadyExists:() -> Unit,
        onSuccess: () -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        val userDocument = firestore.collection("users").document(uid)

        val snapshot = userDocument.get().await()
        if (!snapshot.exists()) {
            val user = User(
                id = uid,
                name = name,
                email = email,
                photo = photo
            )
            userDocument.set(user.toFirestoreMap()).await()
            onSuccess()
        }else{
            alreadyExists()
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val data = userDoc.data
            if (userDoc.exists()) {
                val user = UserfromFirestoreMap(data ?: emptyMap())
                user.id = userDoc.id
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
