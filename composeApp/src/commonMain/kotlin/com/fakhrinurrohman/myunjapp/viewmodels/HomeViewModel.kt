package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.Course
import com.fakhrinurrohman.myunjapp.data.ScheduleRepository
import com.fakhrinurrohman.myunjapp.data.Semester
import com.fakhrinurrohman.myunjapp.data.UserEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

data class HomeUiState(
    val currentSemester: Semester? = null,
    val todaysCourses: List<Course> = emptyList(),
    val upcomingTasks: List<UserEvent> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: ScheduleRepository,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        sessionViewModel.currentSemester,
        sessionViewModel.currentSemester.flatMapLatest { semester ->
            if (semester == null) repository.allCourses
            else repository.getCoursesBySemester(semester.id)
        },
        sessionViewModel.currentSemester.flatMapLatest { semester ->
            if (semester == null) {
                // Combine all events from all semesters
                repository.semesters.flatMapLatest { allSemesters ->
                    val allEventFlows = allSemesters.map { repository.getEventsBySemester(it.id) }
                    if (allEventFlows.isEmpty()) flowOf(emptyList())
                    else combine(allEventFlows) { arrays -> arrays.flatMap { it } }
                }
            } else {
                repository.getEventsBySemester(semester.id)
            }
        }
    ) { semester, courses, events ->
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val todaysCourses = courses.filter { course -> 
            course.daysOfWeek.contains(today.dayOfWeek) 
        }
        val upcomingTasks = events.filter { !it.isCompleted }.sortedBy { it.date }
        
        HomeUiState(
            currentSemester = semester,
            todaysCourses = todaysCourses,
            upcomingTasks = upcomingTasks,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )
}
