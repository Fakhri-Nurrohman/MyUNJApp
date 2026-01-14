package com.fakhrinurrohman.myunjapp.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Immutable
data class CalendarEvent(
    val title: String,
    val description: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val color: Color,
    val type: EventType,
    // Source references
    val semesterId: String? = null,
    val courseId: String? = null,
    val userEventId: String? = null,
    val sourceSemester: Semester? = null,
    val sourceCourse: Course? = null,
    val sourceEvent: UserEvent? = null
)

@Entity
@Immutable
data class Semester(
    @PrimaryKey
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    init {
        require(name.isNotBlank()) { "Semester name cannot be blank" }
    }
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Semester::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("semesterId")]
)
@Immutable
data class Course(
    @PrimaryKey
    val id: String,
    val semesterId: String,
    val userCourseId: String,
    val name: String,
    val teacher: String,
    val room: String,
    val daysOfWeek: List<DayOfWeek>,
    val frequencyWeeks: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val color: Int,
    val isManuallyEdited: Boolean = false // New: tracks if user customized this course
) {
    init {
        require(userCourseId.isNotBlank()) { "Course ID cannot be blank" }
        require(name.isNotBlank()) { "Course name cannot be blank" }
    }
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Semester::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("semesterId"), Index("courseId")]
)
@Immutable
data class UserEvent(
    @PrimaryKey
    val id: String,
    val semesterId: String,
    val courseId: String?,
    val title: String,
    val description: String,
    val type: EventType,
    val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val isCompleted: Boolean = false,
    val color: Int? = null
)

enum class EventType { COURSE, HOMEWORK, EXAM, CUSTOM }

@Immutable
data class ScheduleUiState(
    val selectedView: CalendarView,
    val currentDate: LocalDate,
    val allEvents: List<CalendarEvent>,
    val courses: List<Course>,
    val semester: Semester?,
    val isLoading: Boolean = false
)

enum class CalendarView { DAILY, WEEKLY, MONTHLY, SCHEDULE }
