package com.example.petly.data.repository

import com.example.petly.data.models.Weight
import com.example.petly.utils.fromFirestoreMap
import com.example.petly.utils.toFirestoreMap
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
        val petDocRef = firestore.collection("pets").document(petId)
        val subscription = petDocRef.addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                val weightsIds = it.get("weights") as? List<String> ?: emptyList()
                if (weightsIds.isNotEmpty()) {
                    val weightsRefs = firestore.collection("weights")
                        .whereIn("id", weightsIds)
                    weightsRefs.addSnapshotListener { weightSnapshot, _ ->
                        weightSnapshot?.let { querySnapshot ->
                            val weights = mutableListOf<Weight>()
                            for (document in querySnapshot.documents) {
                                val data = document.data
                                val weight = fromFirestoreMap(data ?: emptyMap())
                                weight.id = document.id
                                weight.let { weights.add(it) }
                            }
                            val sortedWeights = weights.sortedBy { it.date }
                            /* Para ordenar por orden de registro
                            val finalSortedWeights = weightsIds.mapNotNull { id ->
                                sortedWeights.find { it.id == id }
                            }
                             */
                            trySend(sortedWeights)
                        }
                    }
                } else {
                    trySend(emptyList<Weight>())
                }
            }
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
            val weight = fromFirestoreMap(data ?: emptyMap())
            weight.id = weightDoc.id
            weight
        } else {
            null
        }
    }
}
