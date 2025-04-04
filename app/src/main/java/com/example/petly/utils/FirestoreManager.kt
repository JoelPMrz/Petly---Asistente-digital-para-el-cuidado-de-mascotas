package com.example.petly.utils

import android.content.Context
import com.example.petly.data.models.Pet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreManager(context : Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = AuthManager(context)
    var userId = auth.getCurrentUser()?.uid

    suspend fun addPet(pet: Pet){
        pet.ownerId = userId.toString()
        firestore.collection("pets").add(pet).await()
    }

    suspend fun updatePet(pet : Pet){
        val petRef = pet.id?.let{
            firestore.collection("pets").document(it)
        }
        petRef?.set(pet)?.await()
    }

    suspend fun deletePet(petId : String){
        val petRef = firestore.collection("pets").document(petId)
        petRef.delete().await()
    }

    fun getPetsFlow(): Flow<List<Pet>> = callbackFlow {
        val petRefs = firestore.collection("pets").whereEqualTo("ownerId", userId)
        val subscription = petRefs.addSnapshotListener{ snapshot, _ ->
            snapshot?.let{ querySnapshot ->
                val pets = mutableListOf<Pet>()
                for(document in querySnapshot.documents){
                    val pet = document.toObject(Pet::class.java)
                    pet?.id = document.id
                    pet?.let { pets.add(it) }
                }
                trySend(pets).isSuccess
            }

        }
        awaitClose{subscription.remove()}
    }



}