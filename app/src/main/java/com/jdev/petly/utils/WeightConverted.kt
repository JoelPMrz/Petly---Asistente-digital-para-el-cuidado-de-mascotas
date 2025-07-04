package com.jdev.petly.utils

import kotlin.math.pow


fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double {
    val converted = when (fromUnit to toUnit) {
        "Kg" to "Lb" -> value * 2.20462
        "Kg" to "Oz" -> value * 35.274
        "Kg" to "Gr" -> value * 1000.0

        "Lb" to "Kg" -> value / 2.20462
        "Lb" to "Oz" -> value * 16.0
        "Lb" to "Gr" -> (value / 2.20462) * 1000.0

        "Oz" to "Kg" -> value / 35.274
        "Oz" to "Lb" -> value / 16.0
        "Oz" to "Gr" -> (value / 35.274) * 1000.0

        "Gr" to "Kg" -> value / 1000.0
        "Gr" to "Lb" -> (value / 1000.0) * 2.20462
        "Gr" to "Oz" -> (value / 1000.0) * 35.274

        else -> value
    }
    return converted
}


fun Double.truncate(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return kotlin.math.floor(this * factor) / factor
}



