package com.fakhrinurrohman.myunjapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserEvent(event: UserEvent)

    @Update
    suspend fun updateUserEvent(event: UserEvent)

    @Query("DELETE FROM UserEvent WHERE id = :id")
    suspend fun deleteUserEvent(id: String)

    @Query("SELECT * FROM UserEvent WHERE semesterId = :semesterId ORDER BY date ASC, startTime ASC")
    fun getEventsBySemester(semesterId: String): Flow<List<UserEvent>>

    @Query("SELECT * FROM UserEvent WHERE id = :id")
    suspend fun getEventById(id: String): UserEvent?
    
    @Query("UPDATE UserEvent SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: String, isCompleted: Boolean)
}
