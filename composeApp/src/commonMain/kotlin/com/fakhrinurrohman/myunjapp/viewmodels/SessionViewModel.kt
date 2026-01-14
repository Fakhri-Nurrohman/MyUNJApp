package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.Semester
import com.fakhrinurrohman.myunjapp.data.ScheduleRepository
import kotlinx.coroutines.flow.*

/**
 * Shared ViewModel to manage the global "active" session (selected semester).
 */
class SessionViewModel(private val repository: ScheduleRepository) : ViewModel() {

    companion object {
        const val ALL_SEMESTERS_ID = "_ALL_SEMESTERS_"
    }

    private val _selectedSemesterId = MutableStateFlow<String?>(null)
    val selectedSemesterId = _selectedSemesterId.asStateFlow()

    val semesters: StateFlow<List<Semester>> = repository.semesters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * The single source of truth for the active semester.
     * Logic:
     * 1. If ID is ALL_SEMESTERS_ID -> return null (represents All Semesters view)
     * 2. If ID is null (Startup) -> default to the first semester in list
     * 3. Otherwise -> find the semester by ID, fallback to first if not found
     */
    val currentSemester: StateFlow<Semester?> = combine(semesters, _selectedSemesterId) { list, id ->
        when (id) {
            ALL_SEMESTERS_ID -> null
            null -> list.firstOrNull()
            else -> list.find { it.id == id } ?: list.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectSemester(semesterId: String?) {
        _selectedSemesterId.value = semesterId
    }
}
