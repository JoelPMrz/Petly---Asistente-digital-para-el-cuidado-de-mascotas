package com.jdev.petly.data.repository

import com.jdev.petly.data.models.PetInvitation
import com.jdev.petly.data.models.petPetInvitationFromFirestoreMap
import com.jdev.petly.data.models.toFirestoreMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

class PetInvitationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun userId() = auth.currentUser?.uid

    suspend fun addPetInvitation(
        petId: String,
        petName: String,
        fromUserId : String,
        fromUserName: String,
        toUserId: String,
        role: String,
    ) : String {
        val petInvitationRef = firestore.collection("petInvitations").document()
        val petInvitationId = petInvitationRef.id

        val petInvitation = PetInvitation(
            id = petInvitationId,
            petId = petId,
            petName = petName,
            fromUserId = fromUserId,
            fromUserName = fromUserName,
            toUserId =  toUserId,
            role = role,
            accepted = false,
            setAt = LocalDateTime.now()
        )

        petInvitationRef.set(petInvitation.toFirestoreMap()).await()

        return petInvitationId
    }

    suspend fun deletePetInvitation(
        petInvitationId : String
    ){
        val petInvitationRef = firestore.collection("petInvitations").document(petInvitationId)
        petInvitationRef.delete().await()
    }

    fun getPetInvitationsFlow(): Flow<List<PetInvitation>> = callbackFlow {
        if (userId() != null) {
            val petInvitationsRefs = firestore.collection("petInvitations")
                .whereEqualTo("toUserId", userId())
            val subscription = petInvitationsRefs.addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val petInvitations = mutableListOf<PetInvitation>()
                    for (document in querySnapshot.documents) {
                        val data = document.data
                        val petInvitation = petPetInvitationFromFirestoreMap(data ?: emptyMap())
                        petInvitation.id = document.id
                        petInvitation.let { petInvitations.add(it)
                        }
                    }
                    trySend(petInvitations).isSuccess
                }
            }
            awaitClose { subscription.remove() }
        } else {
            throw Exception("User not authenticated")
        }
    }


    suspend fun getPetInvitationById(petInvitationId: String): PetInvitation? {
        return try {
            val petInvitationDoc = firestore.collection("petInvitations").document(petInvitationId).get().await()
            val data = petInvitationDoc.data
            if (petInvitationDoc.exists()) {
                val petInvitation = petPetInvitationFromFirestoreMap(data ?: emptyMap())
                petInvitation.id = petInvitationDoc.id
                petInvitation
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}