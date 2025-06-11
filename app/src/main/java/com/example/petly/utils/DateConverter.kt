package com.example.petly.utils

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import com.example.petly.R
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun LocalDate.toTimestamp(): Timestamp {
    val instant = this.atStartOfDay(ZoneId.systemDefault()).toInstant()
    return Timestamp(Date.from(instant))
}


fun LocalDateTime.toTimestamp(): Timestamp {
    val instant = this.atZone(ZoneId.systemDefault()).toInstant()
    return Timestamp(Date.from(instant))
}

fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return this.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun Timestamp.toLocalTime(): LocalTime {
    return this.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
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

fun parseTime(timeString: String): LocalTime {
    Log.d("TimeParsing", "Parsing time: $timeString")
    return try {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        LocalTime.parse(timeString, timeFormatter)
    } catch (e: Exception) {
        Log.e("TimeParsing", "Error parsing time: ${e.message}")
        LocalTime.now()
    }
}

fun formatLocalDateToString(localDate: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    return localDate.format(formatter)
}

fun formatLocalDateToStringWithDay(localDate: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.getDefault())
    return localDate.format(formatter)
}

fun formatLocalTimeToString(localTime: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    return localTime.format(formatter)
}

fun formatLocalDateTimeToString(localDateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    return localDateTime.format(formatter)
}

fun getAgeFromDate(birthDate: LocalDate?, context: Context): String? {
    if (birthDate == null) return null

    val today = LocalDate.now()
    val period = Period.between(birthDate, today)

    val years = period.years
    val months = period.months
    val days = period.days

    val parts = mutableListOf<String>()

    if (years > 0) {
        val label = if (years == 1) context.getString(R.string.year_singular) else context.getString(R.string.year_plural)
        parts.add("$years $label")
    }
    if (months > 0) {
        val label = if (months == 1) context.getString(R.string.month_singular) else context.getString(R.string.month_plural)
        parts.add("$months $label")
    }
    if (days > 0 || parts.isEmpty()) {
        val label = if (days == 1) context.getString(R.string.day_singular) else context.getString(R.string.day_plural)
        parts.add("$days $label")
    }

    return parts.joinToString(", ")
}

