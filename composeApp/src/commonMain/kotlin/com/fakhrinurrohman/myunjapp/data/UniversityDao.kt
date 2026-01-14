package com.fakhrinurrohman.myunjapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UniversityDao {
    @Query("SELECT * FROM Campus")
    fun getAllCampuses(): Flow<List<Campus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampuses(campuses: List<Campus>)

    @Query("SELECT * FROM Building WHERE campusId = :campusId")
    fun getBuildingsByCampus(campusId: String): Flow<List<Building>>

    @Query("SELECT * FROM Building WHERE id = :buildingId")
    suspend fun getBuildingById(buildingId: String): Building?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildings(buildings: List<Building>)

    @Query("SELECT * FROM Room WHERE buildingId = :buildingId")
    fun getRoomsByBuilding(buildingId: String): Flow<List<Room>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<Room>)

    @Query("SELECT * FROM Faculty")
    fun getAllFaculties(): Flow<List<Faculty>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaculties(faculties: List<Faculty>)

    @Query("SELECT * FROM StudyProgram WHERE facultyId = :facultyId")
    fun getStudyProgramsByFaculty(facultyId: String): Flow<List<StudyProgram>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyPrograms(programs: List<StudyProgram>)

    @Query("SELECT * FROM Lecturer WHERE programId = :programId")
    fun getLecturersByProgram(programId: String): Flow<List<Lecturer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecturers(lecturers: List<Lecturer>)
    
    @Query("SELECT * FROM Lecturer")
    fun getAllLecturers(): Flow<List<Lecturer>>
}
