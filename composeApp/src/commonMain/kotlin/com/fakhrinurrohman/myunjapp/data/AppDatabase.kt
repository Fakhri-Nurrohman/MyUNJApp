package com.fakhrinurrohman.myunjapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Semester::class,
        Course::class,
        UserEvent::class,
        Faculty::class,
        StudyProgram::class,
        Lecturer::class,
        Campus::class,
        Building::class,
        Room::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun semesterDao(): SemesterDao
    abstract fun courseDao(): CourseDao
    abstract fun userEventDao(): UserEventDao
    abstract fun universityDao(): UniversityDao
}

interface DBConstructor : androidx.room.RoomDatabaseConstructor<AppDatabase>
