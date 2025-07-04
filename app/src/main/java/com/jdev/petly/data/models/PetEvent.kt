package com.jdev.petly.data.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class PetEvent {
    abstract val id: String
    abstract val petId: String
    abstract val date: LocalDate
    abstract val time: LocalTime
    abstract val dateTime: LocalDateTime

    data class VeterinaryVisitEvent(val visit: VeterinaryVisit) : PetEvent() {
        override val id = visit.id
        override val petId = visit.petId
        override val date = visit.date
        override val time = visit.time
        override val dateTime: LocalDateTime = LocalDateTime.of(date, time)
    }

    data class NormalEvent(val event: Event) : PetEvent(){
        override val id = event.id
        override val petId = event.petId
        override val date = event.date
        override val time = event.time
        override val dateTime: LocalDateTime = LocalDateTime.of(date, time)
    }

}

fun PetEvent.getPet(pets: List<Pet>): Pet? {
    return pets.find { pet ->
        pet.id == this.petId
    }
}