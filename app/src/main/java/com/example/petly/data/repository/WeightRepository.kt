package com.example.petly.data.repository

import com.example.petly.data.models.Weight
import com.example.petly.data.models.toFirestoreMap
import com.example.petly.data.models.weightFromFirestoreMap
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WeightRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getWeightsFlow(petId: String): Flow<List<Weight>> = callbackFlow {
        val subscription = firestore.collection("weights")
            .whereEqualTo("petId", petId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val weights = snapshot?.documents?.mapNotNull { doc ->
                    val weight = weightFromFirestoreMap(doc.data ?: return@mapNotNull null)
                    weight.id = doc.id
                    weight
                }?.sortedBy { it.time } ?: emptyList()

                trySend(weights)
            }

        awaitClose { subscription.remove() }
    }



    suspend fun addWeightToPet(petId: String, weight: Weight) {
        val petRef = firestore.collection("pets").document(petId)
        val weightRef = firestore.collection("weights").add(weight.toFirestoreMap()).await()
        weight.id = weightRef.id
        updateWeight(weight)
        petRef.update("weights", FieldValue.arrayUnion(weight.id)).await()
    }


    suspend fun updateWeight(weight: Weight) {
        val weightRef = firestore.collection("weights").document(weight.id!!)
        weightRef.set(weight.toFirestoreMap()).await()
    }


    suspend fun deleteWeightFromPet(petId: String, weightId: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("weights", FieldValue.arrayRemove(weightId)).await()
        val weightRef = firestore.collection("weights").document(weightId)
        weightRef.delete().await()
    }


    suspend fun getWeightById(weightId: String): Weight? {
        val weightDoc = firestore.collection("weights").document(weightId).get().await()
        return if (weightDoc.exists()) {
            val data = weightDoc.data
            val weight = weightFromFirestoreMap(data ?: emptyMap())
            weight.id = weightDoc.id
            weight
        } else {
            null
        }
    }
}
