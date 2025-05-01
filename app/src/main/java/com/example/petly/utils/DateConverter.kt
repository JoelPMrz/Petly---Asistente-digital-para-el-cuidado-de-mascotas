package com.example.petly.utils

import android.util.Log
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun LocalDate.toTimestamp(): Timestamp {
    val instant = this.atStartOfDay(ZoneId.systemDefault()).toInstant()
    return Timestamp(Date.from(instant))
}

fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun parseDate(dateString: String): LocalDate {
    Log.d("DateParsing", "Parsing date: $dateString")
    return try {
        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        LocalDate.parse(dateString, dateFormatter)
    } catch (e: Exception) {
        Log.e("DateParsing", "Error parsing date: ${e.message}")
        LocalDate.now()
    }
}

fun formatLocalDateToString(localDate: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("es", "ES"))
    return localDate.format(formatter)
}
