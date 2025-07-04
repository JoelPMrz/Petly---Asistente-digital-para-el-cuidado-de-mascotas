package com.jdev.petly.data.repository

import com.jdev.petly.data.models.PetEvent
import com.jdev.petly.data.models.PetEvent.VeterinaryVisitEvent
import com.jdev.petly.data.models.eventFromFirebase
import com.jdev.petly.data.models.veterinaryVisitFromFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

        val listeners = mutableListOf<ListenerRegistration>()
        val allEvents = mutableListOf<PetEvent>()

        fun emitEvents() {
            val filtered = allEvents
                .filter { it.date in startDate..endDate }
                .groupBy { it.date }
            trySend(filtered)
        }

        petIds.forEach { petId ->
            // Listener para veterinaryVisits
            val visitQuery = firestore.collection("veterinaryVisits")
                .whereEqualTo("petId", petId)

            val visitListener = visitQuery.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val visits = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val visit = veterinaryVisitFromFirebase(data)
                    visit.id = doc.id
                    VeterinaryVisitEvent(visit) as PetEvent
                } ?: emptyList()

                // Elimina eventos anteriores de este petId antes de reemplazar
                allEvents.removeAll { it.petId == petId && it is VeterinaryVisitEvent }
                allEvents.addAll(visits)
                emitEvents()
            }

            listeners.add(visitListener)

            // Listener para events
            val eventQuery = firestore.collection("events")
                .whereEqualTo("petId", petId)

            val eventListener = eventQuery.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val event = eventFromFirebase(data)
                    event.id = doc.id
                    PetEvent.NormalEvent(event)
                } ?: emptyList()

                allEvents.removeAll { it.petId == petId && it is PetEvent.NormalEvent }
                allEvents.addAll(events)
                emitEvents()
            }

            listeners.add(eventListener)
        }

        awaitClose {
            listeners.forEach { it.remove() }
        }
    }


}
