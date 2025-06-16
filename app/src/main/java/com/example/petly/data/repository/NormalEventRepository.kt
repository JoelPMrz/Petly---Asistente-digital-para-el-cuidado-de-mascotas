package com.example.petly.data.repository

import android.media.tv.StreamEventResponse
import com.example.petly.data.models.Event
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.data.models.eventFromFirebase
import com.example.petly.data.models.toFirestoreMap
import com.example.petly.data.models.veterinaryVisitFromFirebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NormalEventRepository @Inject constructor(
    private val firestore: FirebaseFirestore
){
    fun getEventsFlow(petId : String): Flow<List<Event>> = callbackFlow {
        val query = firestore.collection("events")
            .whereEqualTo("petId", petId)

        val listener = query.addSnapshotListener{  snapshot, error ->
            if(error != null){
                close(error)
                return@addSnapshotListener
            }
            val events = snapshot?.documents?.mapNotNull { doc ->
                val event = eventFromFirebase(doc?.data ?: return@mapNotNull null)
                event.id = doc.id
                event
            }?.sortedWith ( compareBy({it.date}, {it.time})) ?: emptyList()

            trySend(events).isSuccess
        }
        awaitClose { listener.remove() }
    }

    suspend fun addEventToPet(petId: String, event: Event){
        val petRef = firestore.collection("pets").document(petId)
        val eventRef = firestore.collection("events").add(event.toFirestoreMap()).await()
        event.id = eventRef.id
        updateEvent(event)
        petRef.update("events", FieldValue.arrayUnion(event.id)).await()
    }


    suspend fun updateEvent(event: Event){
        val eventRef = firestore.collection("events").document(event.id)
        eventRef.set(event.toFirestoreMap()).await()
    }

    suspend fun deleteEvent(petId : String, eventId: String){
        val petRef = firestore.collection("pets").document(petId)
        petRef.update("events", FieldValue.arrayRemove(eventId)).await()
        val eventRef = firestore.collection("events").document(eventId)
        eventRef.delete().await()
    }

    suspend fun getEventById(eventId: String): Event? {
        val eventDoc = firestore.collection("events").document(eventId).get().await()
        return if (eventDoc.exists()) {
            val data = eventDoc.data
            val event = eventFromFirebase(data ?: emptyMap())
            event.id = eventDoc.id
            event
        } else {
            null
        }
    }

}