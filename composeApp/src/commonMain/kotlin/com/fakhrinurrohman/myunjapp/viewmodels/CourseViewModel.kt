package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.Course
import com.fakhrinurrohman.myunjapp.data.ScheduleRepository
import com.fakhrinurrohman.myunjapp.data.Semester
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class CourseUiState(
    val courses: List<Course> = emptyList(),
    val semesters: List<Semester> = emptyList(),
    val currentSemester: Semester? = null,
    val isLoading: Boolean = false
)

class CourseViewModel(
    private val repository: ScheduleRepository,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CourseUiState> = combine(
        sessionViewModel.currentSemester.flatMapLatest { semester ->
            // If semester is null, it means SessionViewModel.ALL_SEMESTERS_ID was selected
            if (semester == null) {
                // Use high-performance query from repository
                repository.allCourses
            } else {
                // Return courses for the specific active semester
                repository.getCoursesBySemester(semester.id)
            }
        },
        repository.semesters,
        sessionViewModel.currentSemester
    ) { courses, semesters, currentSemester ->
        CourseUiState(
            courses = courses,
            semesters = semesters,
            currentSemester = currentSemester,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CourseUiState(isLoading = true)
    )

    // Backward compatibility flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val courses: StateFlow<List<Course>> = uiState.map { it.courses }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCourse(userCourseId: String, name: String, teacher: String, room: String, days: Set<DayOfWeek>, frequency: Int, start: LocalTime, end: LocalTime, color: Int) {
        val semesterId = sessionViewModel.currentSemester.value?.id 
            ?: sessionViewModel.semesters.value.firstOrNull()?.id 
            ?: return
            
        viewModelScope.launch {
            repository.addCourse(
                Course(
                    id = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString() + name, 
                    semesterId = semesterId, 
                    userCourseId = userCourseId, 
                    name = name, 
                    teacher = teacher, 
                    room = room, 
                    daysOfWeek = days.toList(), 
                    frequencyWeeks = frequency, 
                    startTime = start, 
                    endTime = end, 
                    color = color,
                    isManuallyEdited = true
                )
            )
        }
    }

    fun updateCourse(id: String, userCourseId: String, name: String, teacher: String, room: String, days: Set<DayOfWeek>, frequency: Int, start: LocalTime, end: LocalTime, color: Int) {
        val semesterId = sessionViewModel.currentSemester.value?.id 
            ?: sessionViewModel.semesters.value.firstOrNull()?.id 
            ?: return
            
        viewModelScope.launch {
            repository.updateCourse(
                Course(
                    id = id, 
                    semesterId = semesterId, 
                    userCourseId = userCourseId, 
                    name = name, 
                    teacher = teacher, 
                    room = room, 
                    daysOfWeek = days.toList(), 
                    frequencyWeeks = frequency, 
                    startTime = start, 
                    endTime = end, 
                    color = color,
                    isManuallyEdited = true
                )
            )
        }
    }

    fun deleteCourse(id: String) {
        viewModelScope.launch { repository.deleteCourse(id) }
    }
}
