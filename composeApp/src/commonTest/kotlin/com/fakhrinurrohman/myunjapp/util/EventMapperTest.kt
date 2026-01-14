package com.fakhrinurrohman.myunjapp.util

import com.fakhrinurrohman.myunjapp.data.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventMapperTest {

    private val testSemester = Semester(
        id = "sem1",
        name = "Spring 2024",
        startDate = LocalDate(2024, 1, 1), // Monday
        endDate = LocalDate(2024, 1, 31)   // Wednesday
    )

    @Test
    fun `generateCalendarEvents correctly maps recurring weekly course`() {
        val weeklyCourse = Course(
            id = "c1",
            semesterId = "sem1",
            userCourseId = "CS101",
            name = "Intro to CS",
            teacher = "Dr. Smith",
            room = "101",
            daysOfWeek = listOf(DayOfWeek.MONDAY),
            frequencyWeeks = 1,
            startTime = LocalTime(9, 0),
            endTime = LocalTime(10, 0),
            color = 0xFF0000
        )

        val events = EventMapper.generateCalendarEvents(testSemester, listOf(weeklyCourse), emptyList())

        // Jan 2024 has 5 Mondays: 1, 8, 15, 22, 29
        assertEquals(5, events.size)
        assertTrue(events.all { it.type == EventType.COURSE })
        assertEquals("CS101: Intro to CS", events[0].title)
        assertEquals(LocalDate(2024, 1, 1), events[0].start.date)
        assertEquals(LocalDate(2024, 1, 29), events[4].start.date)
    }

    @Test
    fun `generateCalendarEvents correctly maps bi-weekly frequency`() {
        val biWeeklyCourse = Course(
            id = "c2",
            semesterId = "sem1",
            userCourseId = "CS102",
            name = "Data Structures",
            teacher = "Dr. Jones",
            room = "102",
            daysOfWeek = listOf(DayOfWeek.MONDAY),
            frequencyWeeks = 2,
            startTime = LocalTime(11, 0),
            endTime = LocalTime(12, 0),
            color = 0x00FF00
        )

        val events = EventMapper.generateCalendarEvents(testSemester, listOf(biWeeklyCourse), emptyList())

        // Should appear on Jan 1, 15, 29 (Every 2nd Monday)
        assertEquals(3, events.size)
        assertEquals(LocalDate(2024, 1, 1), events[0].start.date)
        assertEquals(LocalDate(2024, 1, 15), events[1].start.date)
        assertEquals(LocalDate(2024, 1, 29), events[2].start.date)
    }

    @Test
    fun `generateCalendarEvents correctly maps and links user events`() {
        val homework = UserEvent(
            id = "e1",
            semesterId = "sem1",
            courseId = "c1",
            title = "HW 1",
            description = "Logic circuits",
            type = EventType.HOMEWORK,
            date = LocalDate(2024, 1, 10),
            startTime = LocalTime(18, 0),
            endTime = LocalTime(19, 0),
            isCompleted = false
        )

        val events = EventMapper.generateCalendarEvents(testSemester, emptyList(), listOf(homework))

        assertEquals(1, events.size)
        val mappedEvent = events[0]
        assertEquals("📝 HW 1", mappedEvent.title)
        assertEquals(EventType.HOMEWORK, mappedEvent.type)
        assertEquals("sem1", mappedEvent.semesterId)
        assertEquals("c1", mappedEvent.courseId)
        assertEquals(homework, mappedEvent.sourceEvent)
    }

    @Test
    fun `generateCalendarEvents excludes events outside semester range`() {
        val outsideEvent = UserEvent(
            id = "e2",
            semesterId = "sem1",
            courseId = null,
            title = "Vacation",
            description = "Break",
            type = EventType.CUSTOM,
            date = LocalDate(2024, 2, 10), // Outside Jan
            startTime = null,
            endTime = null
        )

        val events = EventMapper.generateCalendarEvents(testSemester, emptyList(), listOf(outsideEvent))

        assertEquals(0, events.size)
    }
}
