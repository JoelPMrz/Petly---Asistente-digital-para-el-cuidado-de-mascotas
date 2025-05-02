package com.example.petly.utils

fun isValidMicrochipId(microchipId: String): Boolean {
    return microchipId.length == 12 && microchipId.all { it.isDigit() }
}