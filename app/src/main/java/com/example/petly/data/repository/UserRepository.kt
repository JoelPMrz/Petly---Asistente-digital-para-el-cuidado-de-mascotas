package com.example.petly.data.repository

import android.net.Uri
import com.example.petly.data.models.User
import com.example.petly.data.models.UserfromFirestoreMap
import com.example.petly.data.models.toFirestoreMap
import com.example.petly.utils.FirebaseConstants.DEFAULT_USER_PHOTO_URL
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {


    suspend fun addUser(
        name: String?,
        email: String,
        photo: String? = DEFAULT_USER_PHOTO_URL,
        alreadyExists: () -> Unit,
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
        } else {
            alreadyExists()
        }
    }

    suspend fun updateUserProfilePhoto(userId: String, newPhotoUri: Uri) {
        try {
            val fileRef = FirebaseStorage.getInstance().reference
                .child("photos")
                .child("users")
                .child(userId)
                .child("profile_user_photo.jpg")

            fileRef.putFile(newPhotoUri).await()
            val imageUrl = fileRef.downloadUrl.await().toString()

            val userRef = firestore.collection("users").document(userId)
            val userDoc = userRef.get().await()

            if (userDoc.exists()) {
                userRef.update("photo", imageUrl).await()
            } else {
                throw Exception("Usuario no encontrado")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al actualizar la foto de perfil del usuario")
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
