package com.fakhrinurrohman.myunjapp.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UniversityRepository(private val universityDao: UniversityDao) {
    val campuses: Flow<List<Campus>> = universityDao.getAllCampuses()
    val faculties: Flow<List<Faculty>> = universityDao.getAllFaculties()
    val lecturers: Flow<List<Lecturer>> = universityDao.getAllLecturers()

    init {
        // Automatically seed data on first run
        // We use a global scope here because the Repository is a singleton in Koin
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentCampuses = universityDao.getAllCampuses().first()
                if (currentCampuses.isEmpty()) {
                    seedInitialData()
                }
            } catch (e: Exception) {
                // Handle potential database errors during first-time initialization
                e.printStackTrace()
            }
        }
    }

    private suspend fun seedInitialData() {
        val mockCampuses = listOf(
            Campus("A", "Kampus A (Rawamangun)", "Jl. Rawamangun Muka", -6.1947, 106.8784, "Main campus of State University of Jakarta."),
            Campus("B", "Kampus B (Rawamangun)", "Jl. Pemuda No. 10", -6.1938, 106.8821, "Home to Faculty of Engineering and Economics."),
            Campus("C", "Kampus C (Rawamangun)", "Jl. Rawamangun Muka Raya", -6.1925, 106.8795, "Additional campus facilities in Rawamangun."),
            Campus("D", "Kampus D (Halimun)", "Halimun, Jakarta Selatan", -6.2088, 106.8317, "Campus located in the heart of South Jakarta.")
        )
        val mockBuildings = listOf(
            Building("b1", "A", "Gedung Dewi Sartika", -6.1947, 106.8784, "Administrative building and ceremonial hall."),
            Building("b2", "A", "Gedung Ki Hajar Dewantara", -6.1950, 106.8780, "Classroom and faculty office building."),
            Building("b3", "B", "Gedung Pascasarjana", -6.1938, 106.8821, "Home to Postgraduate programs.")
        )
        val mockRooms = listOf(
            Room("r1", "b1", "Aula Latief Hendraningrat", "Lt. 2", "Aula"),
            Room("r2", "b1", "Ruang Rapat 1", "Lt. 1", "Rapat")
        )
        val mockFaculties = listOf(
            Faculty("f1", "Fakultas Ilmu Pendidikan (FIP)", "Focuses on education and pedagogical research.", "https://fip.unj.ac.id"),
            Faculty("f2", "Fakultas Teknik (FT)", "Innovation and technology development.", "https://ft.unj.ac.id"),
            Faculty("f3", "Fakultas Ekonomi (FE)", "Economic development and business administration.", "https://fe.unj.ac.id")
        )
        val mockPrograms = listOf(
            StudyProgram("p1", "f2", "S1 Sistem Informasi", "Accredited A"),
            StudyProgram("p2", "f2", "S1 Teknik Informatika", "Accredited Unggul")
        )
        val mockLecturers = listOf(
            Lecturer("l1", "Dr. Eng. Muhammad", "19800101", "muhammad@unj.ac.id", "p1", "Artificial Intelligence"),
            Lecturer("l2", "Prof. Siti Aminah", "19750505", "siti@unj.ac.id", "p1", "Human-Computer Interaction")
        )

        universityDao.insertCampuses(mockCampuses)
        universityDao.insertBuildings(mockBuildings)
        universityDao.insertRooms(mockRooms)
        universityDao.insertFaculties(mockFaculties)
        universityDao.insertStudyPrograms(mockPrograms)
        universityDao.insertLecturers(mockLecturers)
    }

    fun getBuildingsByCampus(campusId: String): Flow<List<Building>> = 
        universityDao.getBuildingsByCampus(campusId)

    suspend fun getBuildingById(buildingId: String): Building? =
        universityDao.getBuildingById(buildingId)

    fun getRoomsByBuilding(buildingId: String): Flow<List<Room>> = 
        universityDao.getRoomsByBuilding(buildingId)

    fun getStudyProgramsByFaculty(facultyId: String): Flow<List<StudyProgram>> = 
        universityDao.getStudyProgramsByFaculty(facultyId)

    fun getLecturersByProgram(programId: String): Flow<List<Lecturer>> = 
        universityDao.getLecturersByProgram(programId)

    suspend fun populateInitialData(
        campuses: List<Campus>,
        buildings: List<Building>,
        rooms: List<Room>,
        faculties: List<Faculty>,
        programs: List<StudyProgram>,
        lecturers: List<Lecturer>
    ) {
        universityDao.insertCampuses(campuses)
        universityDao.insertBuildings(buildings)
        universityDao.insertRooms(rooms)
        universityDao.insertFaculties(faculties)
        universityDao.insertStudyPrograms(programs)
        universityDao.insertLecturers(lecturers)
    }
}
