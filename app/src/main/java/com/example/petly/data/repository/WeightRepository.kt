package com.example.petly.data.repository

import com.example.petly.data.models.Weight
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

    // Función para obtener los pesos de una mascota a partir de los IDs de los pesos almacenados en el documento de la mascota
    fun getWeightsFlow(petId: String): Flow<List<Weight>> = callbackFlow {
        val petDocRef = firestore.collection("pets").document(petId)

        val subscription = petDocRef.addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                val weightsIds = it.get("weights") as? List<String> ?: emptyList()

                if (weightsIds.isNotEmpty()) {
                    // Solo realiza la consulta si weightsIds no está vacío
                    val weightsRefs = firestore.collection("weights")
                        .whereIn("id", weightsIds)

                    weightsRefs.addSnapshotListener { weightSnapshot, _ ->
                        weightSnapshot?.let { querySnapshot ->
                            val weights = mutableListOf<Weight>()
                            for (document in querySnapshot.documents) {
                                val weight = document.toObject(Weight::class.java)
                                weight?.id = document.id
                                weight?.let { weights.add(it) }
                            }
                            trySend(weights) // Emitir los pesos obtenidos
                        }
                    }
                } else {
                    // Si no hay pesos, simplemente emitimos una lista vacía
                    trySend(emptyList<Weight>())
                }
            }
        }

        awaitClose { subscription.remove() }
    }


    // Función para agregar un peso a una mascota (agrega el ID del peso al campo 'weights' de la mascota)
    suspend fun addWeightToPet(petId: String, weight: Weight) {
        val petRef = firestore.collection("pets").document(petId)
        val weightRef = firestore.collection("weights").add(weight).await()
        weight.id = weightRef.id
        updateWeight(weight)
        petRef.update("weights", FieldValue.arrayUnion(weight.id)).await()
    }


    suspend fun updateWeight(weight: Weight) {
        val weightRef = firestore.collection("weights").document(weight.id!!)
        weightRef.set(weight).await()  // Solo actualiza el peso en la colección "weights"
    }

    // Función para eliminar un peso de una mascota
    suspend fun deleteWeightFromPet(petId: String, weightId: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("weights", FieldValue.arrayRemove(weightId)).await()  // Eliminar el ID del peso
        val weightRef = firestore.collection("weights").document(weightId)
        weightRef.delete().await()  // Eliminar el documento de peso
    }

    // Función para obtener un peso específico por ID
    suspend fun getWeightById(weightId: String): Weight? {
        val weightDoc = firestore.collection("weights").document(weightId).get().await()
        return if (weightDoc.exists()) {
            val weight = weightDoc.toObject(Weight::class.java)
            weight?.id = weightDoc.id
            weight
        } else {
            null
        }
    }
}
