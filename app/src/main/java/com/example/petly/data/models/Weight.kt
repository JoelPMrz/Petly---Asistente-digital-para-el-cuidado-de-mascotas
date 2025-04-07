package com.example.petly.data.models

import java.time.LocalDate

data class Weight(
    var id: String? = null,
    var value: Double = 0.0,
    var unit: WeightUnit = WeightUnit.KILOGRAMS,
    var date: LocalDate = LocalDate.now(),
    var notes: String? = null
)

enum class WeightUnit(val displayName: String){
    KILOGRAMS("kg"),
    POUNDS("lb"),
    OUNCES("oz")
}

