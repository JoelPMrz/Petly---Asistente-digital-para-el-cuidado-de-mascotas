package com.example.petly.utils

fun isMicrochipIdValid(microchipId: String): Boolean {
    return microchipId.length == 12 && microchipId.all { it.isDigit() }
}

fun isMicrochipIdValidOrEmpty(microchipId: String?): Boolean {
    return microchipId.isNullOrBlank() || (microchipId.length == 12 && microchipId.all { it.isDigit() })
}