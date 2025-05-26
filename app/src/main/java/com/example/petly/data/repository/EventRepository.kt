package com.example.petly.data.repository

import com.example.petly.data.models.PetEvent
import com.example.petly.data.models.PetEvent.VeterinaryVisitEvent
import com.example.petly.data.models.veterinaryVisitFromFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getEventsForPetsByDate(petIds: List<String>, date: LocalDate): Flow<List<PetEvent>> = callbackFlow {
        if (petIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listeners = mutableListOf<() -> Unit>()
        val allEvents = mutableListOf<PetEvent>()

        fun emitEvents() {
            val filtered = allEvents.filter { it.date == date }
                .sortedWith(compareBy({ it.dateTime.toLocalDate() }, { it.dateTime.toLocalTime() }))
            trySend(filtered).isSuccess
        }

        petIds.forEach { petId ->
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
                    VeterinaryVisitEvent(visit)
                } ?: emptyList()

                allEvents.removeAll { it.petId == petId }
                allEvents.addAll(visits)
                emitEvents()
            }

            listeners.add { listener.remove() }
        }

        awaitClose {
            listeners.forEach { it.invoke() }
        }
    }

    fun getEventsForPetsInDateRange(
        petIds: List<String>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Map<LocalDate, List<PetEvent>>> = callbackFlow {
        if (petIds.isEmpty()) {
            trySend(emptyMap())
            close()
            return@callbackFlow
        }

        val listeners = mutableListOf<() -> Unit>()
        val allEvents = mutableListOf<PetEvent>()

        fun emitEvents() {
            val filtered = allEvents
                .filter { it.date in startDate..endDate }
                .groupBy { it.date }
            trySend(filtered).isSuccess
        }

        petIds.forEach { petId ->
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
                    VeterinaryVisitEvent(visit)
                } ?: emptyList()

                allEvents.removeAll { it.petId == petId }
                allEvents.addAll(visits)
                emitEvents()
            }

            listeners.add { listener.remove() }
        }

        awaitClose {
            listeners.forEach { it.invoke() }
        }
    }

}
