package com.example.petly.data.models

import com.example.petly.utils.toLocalDate
import com.example.petly.utils.toTimestamp
import com.example.petly.utils.truncate
import com.google.firebase.Timestamp
import java.time.LocalDate

data class Weight(
    var id: String? = null,
    var petId: String? = null,
    var value: Double = 0.0,
    var unit: String = "Kg",
    var date: LocalDate = LocalDate.now(),
    var notes: String? = null
){
    override fun toString(): String {
        return "id: $id Peso: $value"
    }
}





