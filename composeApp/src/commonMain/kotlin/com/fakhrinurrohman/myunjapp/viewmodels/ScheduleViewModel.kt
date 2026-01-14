package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.CalendarView
import com.fakhrinurrohman.myunjapp.data.ScheduleRepository
import com.fakhrinurrohman.myunjapp.data.ScheduleUiState
import com.fakhrinurrohman.myunjapp.util.EventMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import kotlin.time.Clock

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    private val _selectedView = MutableStateFlow(CalendarView.DAILY)
    private val _currentDate = MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    val currentSemester = sessionViewModel.currentSemester
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val courses = sessionViewModel.currentSemester.flatMapLatest { semester ->
        if (semester == null) repository.allCourses
        else repository.getCoursesBySemester(semester.id)
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val userEvents = sessionViewModel.currentSemester.flatMapLatest { semester ->
        if (semester == null) {
            repository.semesters.flatMapLatest { allSemesters ->
                val allEventFlows = allSemesters.map { repository.getEventsBySemester(it.id) }
                if (allEventFlows.isEmpty()) flowOf(emptyList())
                else combine(allEventFlows) { arrays -> arrays.flatMap { it } }
            }
        } else {
            repository.getEventsBySemester(semester.id)
        }
    }

    val uiState: StateFlow<ScheduleUiState> = combine(
        _selectedView,
        _currentDate,
        currentSemester,
        courses,
        userEvents
    ) { view, date, semester, courses, events ->
        val calendarEvents = if (semester != null) {
            EventMapper.generateCalendarEvents(semester, courses, events)
        } else {
            // For "All Semesters", we need a fake semester range to show everything
            // Or we just show the courses list. For now, empty list for calendar views
            emptyList()
        }

        ScheduleUiState(
            selectedView = view,
            currentDate = date,
            allEvents = calendarEvents,
            courses = courses,
            semester = semester,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope, 
        started = SharingStarted.WhileSubscribed(5000), 
        initialValue = ScheduleUiState(
            selectedView = CalendarView.DAILY, 
            currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault()), 
            allEvents = emptyList(), 
            courses = emptyList(), 
            semester = null,
            isLoading = true
        )
    )

    fun changeView(view: CalendarView) {
        _selectedView.value = view
    }

    fun updateCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun resetToToday() {
        _currentDate.value = Clock.System.todayIn(TimeZone.currentSystemDefault())
    }
}
