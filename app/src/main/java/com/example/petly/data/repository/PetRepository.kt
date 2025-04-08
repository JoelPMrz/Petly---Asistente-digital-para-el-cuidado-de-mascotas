package com.example.petly.data.repository

import com.example.petly.data.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PetRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun userId() = auth.currentUser?.uid


    suspend fun addPet(pet: Pet) {
        if (userId() != null) {
            pet.owners = listOf(userId().toString())
            val documentReference = firestore.collection("pets").add(pet).await()
            pet.id = documentReference.id
            updatePet(pet)
        } else {
            throw Exception("User not authenticated")
        }
    }

    suspend fun updatePet(pet: Pet) {
        val petRef = pet.id.let {
            firestore.collection("pets").document(it!!)
        }
        petRef.set(pet).await()
    }

    suspend fun deletePet(petId: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.delete().await()

        val weightsQuery = firestore.collection("weights")
            .whereEqualTo("petId", petId)
            .get()
            .await()

        for (doc in weightsQuery.documents) {
            doc.reference.delete().await()
        }
    }

    fun getPetsFlow(): Flow<List<Pet>> = callbackFlow {
        if (userId() != null) {
            val petRefs = firestore.collection("pets").whereArrayContains("owners", userId().toString())
            val subscription = petRefs.addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val pets = mutableListOf<Pet>()
                    for (document in querySnapshot.documents) {
                        val pet = document.toObject(Pet::class.java)
                        pet?.id = document.id
                        pet?.let { pets.add(it) }
                    }
                    trySend(pets).isSuccess
                }
            }
            awaitClose { subscription.remove() }
        } else {
            throw Exception("User not authenticated")
        }
    }

    suspend fun getPetById(petId: String): Pet? {
        return try {
            val petDoc = firestore.collection("pets").document(petId).get().await()
            if (petDoc.exists()) {
                val pet = petDoc.toObject(Pet::class.java)
                pet?.id = petDoc.id
                pet
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
