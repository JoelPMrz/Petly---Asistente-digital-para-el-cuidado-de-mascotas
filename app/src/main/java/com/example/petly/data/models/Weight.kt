package com.example.petly.data.models

import java.time.LocalDate
import java.time.LocalTime

data class Weight(
    var id: String? = null,
    var petId: String? = null,
    var value: Double = 0.0,
    var unit: String = "Kg",
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now(),
    var notes: String? = null
){
    override fun toString(): String {
        return "id: $id Peso: $value"
    }
}





