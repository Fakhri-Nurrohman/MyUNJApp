package com.fakhrinurrohman.myunjapp.util

import kotlinx.datetime.*

fun LocalDate.toRelativeDateString(): String {
    val today = kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysUntil = today.daysUntil(this)

    return when {
        daysUntil == -1 -> "Yesterday"
        daysUntil == 0 -> "Today"
        daysUntil == 1 -> "Tomorrow"
        daysUntil in 2..7 -> "In $daysUntil days"
        daysUntil > 7 -> toReadableDate()
        daysUntil < -1 -> "${daysUntil.absoluteValue} days ago"
        else -> toReadableDate()
    }
}

private val Int.absoluteValue: Int get() = if (this < 0) -this else this

fun LocalDate.toReadableDate(): String {
    val monthName = this.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "${this.day} $monthName ${this.year}"
}

fun LocalDate.toFullReadableDate(): String {
    val dayName = this.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val monthName = this.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$dayName, ${this.day} $monthName ${this.year}"
}

fun LocalDate.toMonthYear(): String {
    val monthName = this.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$monthName ${this.year}"
}

fun LocalTime.toReadableTime(): String {
    return "${this.hour.toString().padStart(2, '0')}:${this.minute.toString().padStart(2, '0')}"
}
