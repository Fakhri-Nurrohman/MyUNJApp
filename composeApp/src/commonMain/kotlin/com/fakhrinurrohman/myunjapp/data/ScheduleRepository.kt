package com.fakhrinurrohman.myunjapp.data

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(
    private val semesterDao: SemesterDao,
    private val courseDao: CourseDao,
    private val userEventDao: UserEventDao
) {
    val semesters: Flow<List<Semester>> = semesterDao.getAllSemesters()

    suspend fun addSemester(semester: Semester) {
        semesterDao.insertSemester(semester)
    }

    suspend fun updateSemester(semester: Semester) {
        semesterDao.updateSemester(semester)
    }

    suspend fun deleteSemester(id: String) {
        semesterDao.deleteSemester(id)
    }

    suspend fun getSemesterById(id: String): Semester? {
        return semesterDao.getSemesterById(id)
    }

    // Course operations
    fun getCoursesBySemester(semesterId: String): Flow<List<Course>> {
        return courseDao.getCoursesBySemester(semesterId)
    }

    // High-performance query for all courses across all semesters
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    suspend fun addCourse(course: Course) {
        courseDao.insertCourse(course)
    }

    suspend fun updateCourse(course: Course) {
        courseDao.updateCourse(course)
    }

    suspend fun deleteCourse(id: String) {
        courseDao.deleteCourse(id)
    }

    suspend fun getCourseById(id: String): Course? {
        return courseDao.getCourseById(id)
    }

    // User Event (Tasks, Exams, etc.) operations
    fun getEventsBySemester(semesterId: String): Flow<List<UserEvent>> {
        return userEventDao.getEventsBySemester(semesterId)
    }

    suspend fun addUserEvent(event: UserEvent) {
        userEventDao.insertUserEvent(event)
    }

    suspend fun updateUserEvent(event: UserEvent) {
        userEventDao.updateUserEvent(event)
    }

    suspend fun deleteUserEvent(id: String) {
        userEventDao.deleteUserEvent(id)
    }

    suspend fun updateEventCompletion(id: String, isCompleted: Boolean) {
        userEventDao.updateCompletionStatus(id, isCompleted)
    }
}
