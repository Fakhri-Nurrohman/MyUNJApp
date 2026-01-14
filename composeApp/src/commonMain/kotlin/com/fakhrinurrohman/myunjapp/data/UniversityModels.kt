package com.fakhrinurrohman.myunjapp.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
@Immutable
data class Campus(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val description: String? = null,
    val imageUrl: String? = null
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Campus::class,
            parentColumns = ["id"],
            childColumns = ["campusId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("campusId")]
)
@Immutable
data class Building(
    @PrimaryKey val id: String,
    val campusId: String,
    val name: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val description: String? = null,
    val imageUrl: String? = null
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Building::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        )
  ],
    indices = [Index("buildingId")]
)
@Immutable
data class Room(
    @PrimaryKey val id: String,
    val buildingId: String,
    val name: String,
    val floor: String? = null,
    val type: String? = null // e.g., "Laboratorium", "Kelas", "Aula"
)

@Entity
@Immutable
data class Faculty(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
    val website: String? = null
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("facultyId")]
)
@Immutable
data class StudyProgram(
    @PrimaryKey val id: String,
    val facultyId: String,
    val name: String,
    val accreditation: String? = null // e.g., "Unggul", "A"
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = StudyProgram::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("programId")]
)
@Immutable
data class Lecturer(
    @PrimaryKey val id: String,
    val name: String,
    val nip: String? = null,
    val email: String? = null,
    val programId: String,
    val expertise: String? = null // Field of research/interest
)

@Immutable
data class UnjNews(
    val id: String,
    val title: String,
    val date: String,
    val url: String,
    val category: String,
    val imageUrl: String? = null,
    val summary: String? = null
)
