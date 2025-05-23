package com.example.petly.utils

import java.text.Normalizer

fun String.normalizeForSearch(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace("\\p{M}".toRegex(), "")
        .lowercase()
}