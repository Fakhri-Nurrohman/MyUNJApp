package com.fakhrinurrohman.myunjapp.util

import androidx.compose.ui.graphics.Color
import com.fakhrinurrohman.myunjapp.data.CalendarEvent
import com.fakhrinurrohman.myunjapp.data.Course
import com.fakhrinurrohman.myunjapp.data.EventType
import com.fakhrinurrohman.myunjapp.data.Semester
import com.fakhrinurrohman.myunjapp.data.UserEvent
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

object EventMapper {

    fun generateCalendarEvents(
        semester: Semester,
        courseList: List<Course>,
        eventList: List<UserEvent>
    ): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        
        // 1. Process Recurring Courses
        val totalDays = semester.startDate.daysUntil(semester.endDate) + 1
        for (i in 0 until totalDays) {
            val date = semester.startDate.plus(i, DateTimeUnit.DAY)
            val dayCourses = courseList.filter { course ->
                val isCorrectDay = course.daysOfWeek.contains(date.dayOfWeek)
                val weeksSinceStart = (semester.startDate.daysUntil(date) / 7)
                val isCorrectWeek = (weeksSinceStart % course.frequencyWeeks) == 0
                isCorrectDay && isCorrectWeek
            }
            
            dayCourses.forEach { course ->
                events.add(
                    CalendarEvent(
                        title = "${course.userCourseId}: ${course.name}",
                        description = "Teacher: ${course.teacher}\nRoom: ${course.room}",
                        start = LocalDateTime(date, course.startTime),
                        end = LocalDateTime(date, course.endTime),
                        color = Color(course.color),
                        type = EventType.COURSE,
                        semesterId = semester.id,
                        courseId = course.id,
                        sourceSemester = semester,
                        sourceCourse = course
                    )
                )
            }
        }

        // 2. Process One-time UserEvents
        eventList.forEach { userEvent ->
            if (userEvent.date >= semester.startDate && userEvent.date <= semester.endDate) {
                val baseColor = if (userEvent.color != null) {
                    Color(userEvent.color)
                } else {
                    val linkedCourse = courseList.find { it.id == userEvent.courseId }
                    linkedCourse?.let { Color(it.color) } ?: getFallbackColor(userEvent.type)
                }

                val start = LocalDateTime(userEvent.date, userEvent.startTime ?: LocalTime(0, 0))
                val end = LocalDateTime(userEvent.date, userEvent.endTime ?: LocalTime(23, 59))

                events.add(
                    CalendarEvent(
                        title = if (userEvent.type == EventType.HOMEWORK) "📝 ${userEvent.title}" else userEvent.title,
                        description = userEvent.description,
                        start = start,
                        end = end,
                        color = baseColor,
                        type = userEvent.type,
                        semesterId = semester.id,
                        courseId = userEvent.courseId,
                        userEventId = userEvent.id,
                        sourceSemester = semester,
                        sourceCourse = courseList.find { it.id == userEvent.courseId },
                        sourceEvent = userEvent
                    )
                )
            }
        }

        return events.sortedBy { it.start }
    }

    private fun getFallbackColor(type: EventType): Color = when(type) {
        EventType.HOMEWORK -> Color(0xFFFFA000) // Amber
        EventType.EXAM -> Color(0xFFD32F2F) // Red
        EventType.CUSTOM -> Color(0xFF1976D2) // Blue
        else -> Color.Gray
    }
}
