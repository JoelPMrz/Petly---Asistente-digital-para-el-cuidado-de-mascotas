package com.example.petly.data.repository

import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.data.models.Weight
import com.example.petly.data.models.toFirestoreMap
import com.example.petly.data.models.veterinaryVisitFromFirebase
import com.example.petly.data.models.weightFromFirestoreMap
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VeterinaryVisitsRepository @Inject constructor(
    private val firestore : FirebaseFirestore
) {

    fun getVeterinaryVisitsFlow(petId: String): Flow<List<VeterinaryVisit>> = callbackFlow {
        val veterinaryVisitRef = firestore.collection("veterinaryVisits").document(petId)
        val subscription = veterinaryVisitRef.addSnapshotListener{ snapshot, _ ->
            snapshot?.let {
                val veterinaryVisitIds = it.get("veterinaryVisits")  as? List<String> ?: emptyList()
                if(veterinaryVisitIds.isNotEmpty()){
                    val veterinaryVisitRefs = firestore.collection("veterinaryVisits")
                        .whereIn("id", veterinaryVisitIds)
                    veterinaryVisitRefs.addSnapshotListener { veterinaryVisitSnapshot, _ ->
                        veterinaryVisitSnapshot?.let { querySnapshot ->
                            val veterinaryVisits = mutableListOf<VeterinaryVisit>()
                            for (document in querySnapshot.documents) {
                                val data = document.data
                                val veterinaryVisit = veterinaryVisitFromFirebase(data ?: emptyMap())
                                veterinaryVisit.id = document.id
                                veterinaryVisit.let { veterinaryVisits.add(it) }
                            }
                            val sortedVeterinaryVisits = veterinaryVisits
                                .sortedWith(compareBy<VeterinaryVisit> { it.time })

                            trySend(sortedVeterinaryVisits)
                        }
                    }
                } else {
                    trySend(emptyList<VeterinaryVisit>())
                }
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun addVeterinaryVisitToPet(petId: String, veterinaryVisit: VeterinaryVisit) {
        val petRef = firestore.collection("pets").document(petId)
        val veterinaryVisitRef = firestore.collection("veterinaryVisits").add(veterinaryVisit.toFirestoreMap()).await()
        veterinaryVisit.id = veterinaryVisitRef.id
        updateVeterinaryVisit(veterinaryVisit)
        petRef.update("veterinaryVisits", FieldValue.arrayUnion(veterinaryVisit.id)).await()
    }


    suspend fun updateVeterinaryVisit(veterinaryVisit: VeterinaryVisit) {
        val veterinaryVisitRef = firestore.collection("veterinaryVisits").document(veterinaryVisit.id)
        veterinaryVisitRef.set(veterinaryVisit.toFirestoreMap()).await()
    }


    suspend fun deleteVeterinaryVisitFromPet(petId: String, veterinaryVisitId: String) {
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("veterinaryVisits", FieldValue.arrayRemove(veterinaryVisitId)).await()
        val veterinaryVisitRef = firestore.collection("veterinaryVisits").document(veterinaryVisitId)
        veterinaryVisitRef.delete().await()
    }


    suspend fun getWeightById(weightId: String): VeterinaryVisit? {
        val veterinaryVisitDoc = firestore.collection("veterinaryVisits").document(weightId).get().await()
        return if (veterinaryVisitDoc.exists()) {
            val data = veterinaryVisitDoc.data
            val veterinaryVisit = veterinaryVisitFromFirebase(data ?: emptyMap())
            veterinaryVisit.id = veterinaryVisitDoc.id
            veterinaryVisit
        } else {
            null
        }
    }

}