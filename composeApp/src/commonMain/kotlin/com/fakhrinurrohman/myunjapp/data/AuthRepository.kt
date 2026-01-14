package com.fakhrinurrohman.myunjapp.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.random.Random

class AuthRepository(
    private val apiService: SiakadApiService,
    private val scheduleRepository: ScheduleRepository
) {
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private var _authToken = MutableStateFlow<String?>(null)
    val authToken = _authToken.asStateFlow()

    val isLoggedIn: Boolean get() = _authToken.value != null

    suspend fun loginAndSync(nim: String, password: String): SiakadLoginResponse {
        val loginResponse = apiService.login(nim, password)
        
        if (loginResponse.success && loginResponse.token != null) {
            _authToken.value = loginResponse.token
            syncSchedule(loginResponse.token)
        }
        
        return loginResponse
    }

    suspend fun performSync(): Boolean {
        val token = _authToken.value ?: return false
        syncSchedule(token)
        return true
    }

    private suspend fun syncSchedule(token: String) {
        _isSyncing.value = true
        try {
            kotlinx.coroutines.delay(1500)
            val remoteData = apiService.fetchSchedule(token)
            
            val semesterId = remoteData.semesterName.filter { it.isLetterOrDigit() }
            val semester = Semester(
                id = semesterId,
                name = remoteData.semesterName,
                startDate = LocalDate.parse(remoteData.startDate),
                endDate = LocalDate.parse(remoteData.endDate)
            )
            scheduleRepository.addSemester(semester)

            // Get existing courses to check for manual edits
            val existingCourses = scheduleRepository.allCourses.first()

            remoteData.courses.forEach { remoteCourse ->
                val existing = existingCourses.find { it.id == remoteCourse.id }
                
                val localCourse = Course(
                    id = remoteCourse.id,
                    semesterId = semesterId,
                    userCourseId = remoteCourse.code,
                    name = remoteCourse.name,
                    teacher = remoteCourse.lecturer,
                    room = remoteCourse.room,
                    daysOfWeek = listOf(dayIntToEnum(remoteCourse.dayOfWeek)),
                    frequencyWeeks = 1,
                    startTime = LocalTime.parse(remoteCourse.startTime),
                    endTime = LocalTime.parse(remoteCourse.endTime),
                    // Protect user color if they edited it manually
                    color = if (existing?.isManuallyEdited == true) existing.color else (remoteCourse.color ?: generateRandomColor()),
                    isManuallyEdited = existing?.isManuallyEdited ?: false
                )
                scheduleRepository.addCourse(localCourse)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isSyncing.value = false
        }
    }

    private fun dayIntToEnum(day: Int) = when(day) {
        1 -> kotlinx.datetime.DayOfWeek.MONDAY
        2 -> kotlinx.datetime.DayOfWeek.TUESDAY
        3 -> kotlinx.datetime.DayOfWeek.WEDNESDAY
        4 -> kotlinx.datetime.DayOfWeek.THURSDAY
        5 -> kotlinx.datetime.DayOfWeek.FRIDAY
        6 -> kotlinx.datetime.DayOfWeek.SATURDAY
        else -> kotlinx.datetime.DayOfWeek.SUNDAY
    }

    private fun generateRandomColor(): Int {
        val colors = listOf(0xFFE57373, 0xFF81C784, 0xFF64B5F6, 0xFFFFD54F, 0xFFBA68C8)
        return colors[Random.nextInt(colors.size)].toInt()
    }
}
