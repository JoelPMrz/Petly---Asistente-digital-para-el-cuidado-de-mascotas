package com.example.petly.data.repository

import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.data.models.toFirestoreMap
import com.example.petly.data.models.veterinaryVisitFromFirebase
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
        val query = firestore.collection("veterinaryVisits")
            .whereEqualTo("petId", petId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val visits = snapshot?.documents?.mapNotNull { doc ->
                val visit = veterinaryVisitFromFirebase(doc.data ?: return@mapNotNull null)
                visit.id = doc.id
                visit
            }?.sortedWith(compareBy({ it.date }, { it.time })) ?: emptyList()

            trySend(visits).isSuccess
        }

        awaitClose { listener.remove() }
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


    suspend fun getVeterinaryVisitById(veterinaryVisitId: String): VeterinaryVisit? {
        val veterinaryVisitDoc = firestore.collection("veterinaryVisits").document(veterinaryVisitId).get().await()
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