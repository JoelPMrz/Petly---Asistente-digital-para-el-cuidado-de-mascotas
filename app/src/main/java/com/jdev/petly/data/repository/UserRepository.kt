package com.jdev.petly.data.repository

import android.net.Uri
import com.jdev.petly.data.models.User
import com.jdev.petly.data.models.toFirestoreMap
import com.jdev.petly.data.models.userfromFirestoreMap
import com.jdev.petly.utils.FirebaseConstants.DEFAULT_USER_PHOTO_URL
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                name = name ?: "user$uid",
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

    suspend fun updateUserBasicData(
        userId : String,
        name: String?,
    ){
        val userRef = firestore.collection("users").document(userId)
        val userDoc = userRef.get().await()
        if (userDoc.exists()) {
            userRef.update("name", name).await()
        } else {
            throw Exception("Usuario no encontrado")
        }
    }


    suspend fun getUserById(userId: String): User? {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val data = userDoc.data
            if (userDoc.exists()) {
                val user = userfromFirestoreMap(data ?: emptyMap())
                user.id = userDoc.id
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getUserByIdFlow(userId: String): Flow<User?> = callbackFlow {
        val listenerRegistration = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = userfromFirestoreMap(snapshot.data ?: emptyMap())
                    user.id = snapshot.id
                    trySend(user)
                } else {
                    trySend(null)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }


    fun getUsersFromPetByRoleFlow(petId: String, roleField: String): Flow<List<User>> = callbackFlow {
        val petDocRef = firestore.collection("pets").document(petId)

        var userListeners = listOf<ListenerRegistration>()

        val petListener = petDocRef.addSnapshotListener { snapshot, _ ->
            snapshot?.let { documentSnapshot ->
                val roleUserIds = documentSnapshot.get(roleField) as? List<String> ?: emptyList()

                // Cancela los listeners anteriores si cambia la lista
                userListeners.forEach { it.remove() }
                userListeners = emptyList()

                if (roleUserIds.isEmpty()) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                val remaining = roleUserIds.size
                val tempList = MutableList<User?>(remaining) { null }

                roleUserIds.forEachIndexed { index, userId ->
                    val listener = firestore.collection("users").document(userId)
                        .addSnapshotListener { userSnap, _ ->
                            if (userSnap != null && userSnap.exists()) {
                                val data = userSnap.data ?: return@addSnapshotListener
                                val user = userfromFirestoreMap(data).apply { id = userSnap.id }
                                tempList[index] = user

                                if (tempList.none { it == null }) {
                                    trySend(tempList.filterNotNull()).isSuccess
                                }
                            }
                        }

                    userListeners = userListeners + listener
                }
            }
        }

        awaitClose {
            petListener.remove()
            userListeners.forEach { it.remove() }
        }
    }
}
