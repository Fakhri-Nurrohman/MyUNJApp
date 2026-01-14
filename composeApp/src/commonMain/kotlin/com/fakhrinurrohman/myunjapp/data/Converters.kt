package com.fakhrinurrohman.myunjapp.data

import androidx.room.TypeConverter
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromDayOfWeek(day: DayOfWeek?): Int? {
        return day?.ordinal
    }

    @TypeConverter
    fun toDayOfWeek(ordinal: Int?): DayOfWeek? {
        return ordinal?.let { DayOfWeek.values()[it] }
    }

    // New converters for List<DayOfWeek>
    @TypeConverter
    fun fromDaysOfWeek(days: List<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDaysOfWeek(daysString: String?): List<DayOfWeek>? {
        return daysString?.split(",")?.filter { it.isNotEmpty() }?.map { DayOfWeek.valueOf(it) }
    }
}
