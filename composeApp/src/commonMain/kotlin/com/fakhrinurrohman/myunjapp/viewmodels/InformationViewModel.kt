package com.fakhrinurrohman.myunjapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhrinurrohman.myunjapp.data.*
import kotlinx.coroutines.flow.*

data class InformationUiState(
    val campuses: List<Campus> = emptyList(),
    val faculties: List<Faculty> = emptyList(),
    val lecturers: List<Lecturer> = emptyList(),
    val news: List<UnjNews> = emptyList(),
    val selectedNewsCategory: String = "Semua",
    val isLoading: Boolean = false
)

class InformationViewModel(private val repository: UniversityRepository) : ViewModel() {

    private val mockNews = listOf(
        UnjNews("1", "Wisuda Semester 119", "2024-03-15", "https://unj.ac.id", "Terbaru"),
        UnjNews("2", "Prestasi Internasional Mahasiswa", "2024-03-10", "https://unj.ac.id", "Prestasi"),
        UnjNews("3", "Inovasi Panel Surya Murah", "2024-03-05", "https://unj.ac.id", "Riset & Inovasi"),
        UnjNews("4", "Pengabdian Masyarakat di Desa", "2024-03-01", "https://unj.ac.id", "Pengabdian"),
        UnjNews("5", "Lomba Debat Nasional", "2024-02-25", "https://unj.ac.id", "Prestasi")
    )

    private val _selectedNewsCategory = MutableStateFlow("Semua")
    val selectedNewsCategory = _selectedNewsCategory.asStateFlow()

    val uiState: StateFlow<InformationUiState> = combine(
        repository.campuses,
        repository.faculties,
        repository.lecturers,
        _selectedNewsCategory
    ) { campuses, faculties, lecturers, category ->
        val filteredNews = if (category == "Semua") mockNews else mockNews.filter { it.category == category }
        InformationUiState(
            campuses = campuses,
            faculties = faculties,
            lecturers = lecturers,
            news = filteredNews,
            selectedNewsCategory = category,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InformationUiState(isLoading = true)
    )

    fun selectNewsCategory(category: String) {
        _selectedNewsCategory.value = category
    }

    // Fixed single item helpers
    fun getCampus(id: String): Flow<Campus?> = uiState.map { state -> state.campuses.find { it.id == id } }
    
    // Proper building lookup for inherited coordinates
    suspend fun getBuildingById(id: String): Building? = repository.getBuildingById(id)
    
    // Drill-down helpers
    fun getBuildings(campusId: String): Flow<List<Building>> = repository.getBuildingsByCampus(campusId)
    fun getRooms(buildingId: String): Flow<List<Room>> = repository.getRoomsByBuilding(buildingId)
    fun getStudyPrograms(facultyId: String): Flow<List<StudyProgram>> = repository.getStudyProgramsByFaculty(facultyId)
    fun getLecturersByProgram(programId: String): Flow<List<Lecturer>> = repository.getLecturersByProgram(programId)
}
