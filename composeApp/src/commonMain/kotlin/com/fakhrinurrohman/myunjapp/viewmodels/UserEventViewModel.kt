package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class UserEventUiState(
    val userEvents: List<UserEvent> = emptyList(),
    val currentSemester: Semester? = null,
    val isLoading: Boolean = false
)

class UserEventViewModel(
    private val repository: ScheduleRepository,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UserEventUiState> = combine(
        sessionViewModel.currentSemester.flatMapLatest { semester ->
            semester?.let { repository.getEventsBySemester(it.id) } ?: flowOf(emptyList())
        },
        sessionViewModel.currentSemester
    ) { events, semester ->
        UserEventUiState(
            userEvents = events,
            currentSemester = semester
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserEventUiState(isLoading = true)
    )

    val currentSemester = sessionViewModel.currentSemester
    @OptIn(ExperimentalCoroutinesApi::class)
    val userEvents: StateFlow<List<UserEvent>> = currentSemester.flatMapLatest { semester ->
        semester?.let { repository.getEventsBySemester(it.id) } ?: flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addUserEvent(title: String,
                     description: String,
                     type: EventType,
                     date: LocalDate,
                     startTime: LocalTime?,
                     endTime: LocalTime?,
                     courseId: String?,
                     color: Int?) {
        val semesterId = sessionViewModel.currentSemester.value?.id ?: return
        viewModelScope.launch {
            repository.addUserEvent(
                UserEvent(
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).toString() + title,
                    semesterId, courseId, title, description, type, date, startTime, endTime, false, color))
        }
    }

    fun updateUserEvent(
        id: String,
        title: String,
        description: String,
        type: EventType,
        date: LocalDate,
        startTime: LocalTime?,
        endTime: LocalTime?,
        courseId: String?,
        isCompleted: Boolean,
        color: Int?) {
        val semesterId = sessionViewModel.currentSemester.value?.id ?: return
        viewModelScope.launch { repository.updateUserEvent(
            UserEvent(id, semesterId, courseId, title, description, type, date, startTime, endTime, isCompleted, color)) }
    }

    fun deleteUserEvent(id: String) {
        viewModelScope.launch { repository.deleteUserEvent(id) }
    }

    fun toggleTaskCompletion(id: String, isCompleted: Boolean) {
        viewModelScope.launch { repository.updateEventCompletion(id, isCompleted) }
    }
}
