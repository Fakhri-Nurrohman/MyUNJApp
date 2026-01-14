package com.fakhrinurrohman.myunjapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Update
    suspend fun updateCourse(course: Course)

    @Query("DELETE FROM Course WHERE id = :id")
    suspend fun deleteCourse(id: String)

    @Query("SELECT * FROM Course WHERE semesterId = :semesterId ORDER BY daysOfWeek ASC, startTime ASC")
    fun getCoursesBySemester(semesterId: String): Flow<List<Course>>

    @Query("SELECT * FROM Course ORDER BY daysOfWeek ASC, startTime ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM Course WHERE id = :id")
    suspend fun getCourseById(id: String): Course?
}
