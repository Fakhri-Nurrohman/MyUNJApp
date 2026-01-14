package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.Semester
import com.fakhrinurrohman.myunjapp.data.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

data class SemesterUiState(
    val semesters: List<Semester> = emptyList(),
    val currentSemester: Semester? = null,
    val isLoading: Boolean = false
)

class SemesterViewModel(
    private val repository: ScheduleRepository,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    val uiState: StateFlow<SemesterUiState> = combine(
        repository.semesters,
        sessionViewModel.currentSemester
    ) { semesters, currentSemester ->
        SemesterUiState(
            semesters = semesters,
            currentSemester = currentSemester
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SemesterUiState(isLoading = true)
    )

    // Keep these for backward compatibility if needed, but uiState is preferred
    val semesters: StateFlow<List<Semester>> = repository.semesters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentSemester = sessionViewModel.currentSemester

    fun addSemester(name: String, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            val id = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString() + name
            repository.addSemester(Semester(id, name, startDate, endDate))
        }
    }

    fun updateSemester(id: String, name: String, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            repository.updateSemester(Semester(id, name, startDate, endDate))
        }
    }

    fun deleteSemester(id: String) {
        viewModelScope.launch {
            repository.deleteSemester(id)
        }
    }

    fun selectSemester(semester: Semester?) {
        sessionViewModel.selectSemester(semester?.id)
    }
}
