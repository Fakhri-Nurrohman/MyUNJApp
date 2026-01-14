package com.fakhrinurrohman.myunjapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester)

    @Update
    suspend fun updateSemester(semester: Semester)

    @Query("DELETE FROM Semester WHERE id = :id")
    suspend fun deleteSemester(id: String)

    @Query("SELECT * FROM Semester ORDER BY startDate ASC")
    fun getAllSemesters(): Flow<List<Semester>>

    @Query("SELECT * FROM Semester WHERE id = :id")
    suspend fun getSemesterById(id: String): Semester?
}
