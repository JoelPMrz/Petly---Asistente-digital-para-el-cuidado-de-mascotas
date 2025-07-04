package com.jdev.petly.utils

fun isVersionLower(currentVersion: String, comparedVersion: String): Boolean {
    val currentParts = currentVersion.split(".")
    val comparedParts = comparedVersion.split(".")

    val maxLength = maxOf(currentParts.size, comparedParts.size)

    for (i in 0 until maxLength) {
        val currentPart = currentParts.getOrNull(i)?.toIntOrNull() ?: 0
        val comparedPart = comparedParts.getOrNull(i)?.toIntOrNull() ?: 0

        if (currentPart < comparedPart) return true
        if (currentPart > comparedPart) return false
    }
    return false
}
