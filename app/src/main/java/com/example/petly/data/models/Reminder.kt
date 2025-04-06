package com.example.petly.data.models

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class Reminder(
    var id: String? = null,
    var note: String = "",
    var startDateTime: LocalDateTime,       // Fecha de inicio del recordatorio
    var frequencyAmount: Int? = null,       // Frecuencia en caso de repetición
    var frequencyUnit: FrequencyUnit? = null,
    var endDateTime: LocalDateTime? = null, // Fecha de fin en caso de repetición
    var isCompleted: Boolean = false,
    var notificationLeadTime: NotificationLeadTime? = null // Anticipación de la notificación
) {
    enum class FrequencyUnit {
        HOURS, DAYS, WEEKS, MONTHS, YEARS
    }

    // Este enum representa los diferentes tiempos de antelación para la notificación
    enum class NotificationLeadTime(val value: Long, val unit: ChronoUnit) {
        ONE_HOUR(1, ChronoUnit.HOURS),
        ONE_DAY(1, ChronoUnit.DAYS),
        TWO_DAYS(2, ChronoUnit.DAYS),
        ONE_WEEK(1, ChronoUnit.WEEKS)
    }

    // Función que calcula la fecha para la notificación basada en el `notificationLeadTime`
    fun calculateNotificationDate(): LocalDateTime? {
        return notificationLeadTime?.let {
            startDateTime.minus(it.value, it.unit)
        }
    }

    // Función que calcula la próxima fecha del recordatorio si hay frecuencia
    fun calculateNextReminderDate(): LocalDateTime {
        return when (frequencyUnit) {
            FrequencyUnit.HOURS -> startDateTime.plusHours(frequencyAmount!!.toLong())
            FrequencyUnit.DAYS -> startDateTime.plusDays(frequencyAmount!!.toLong())
            FrequencyUnit.WEEKS -> startDateTime.plusWeeks(frequencyAmount!!.toLong())
            FrequencyUnit.MONTHS -> startDateTime.plusMonths(frequencyAmount!!.toLong())
            FrequencyUnit.YEARS -> startDateTime.plusYears(frequencyAmount!!.toLong())
            else -> startDateTime
        }
    }
}
