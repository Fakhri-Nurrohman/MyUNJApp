package com.fakhrinurrohman.myunjapp.data

import kotlinx.serialization.Serializable

/**
 * Data structures for SIAKAD API integration.
 */

@Serializable
data class SiakadLoginResponse(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null,
    val nim: String? = null,
    val name: String? = null
)

@Serializable
data class SiakadScheduleResponse(
    val semesterName: String,
    val startDate: String, // ISO format
    val endDate: String,
    val courses: List<SiakadCourse>
)

@Serializable
data class SiakadCourse(
    val id: String,
    val code: String,
    val name: String,
    val lecturer: String,
    val room: String,
    val dayOfWeek: Int, // 1 (Monday) to 7 (Sunday)
    val startTime: String, // "HH:mm"
    val endTime: String,
    val color: Int? = null
)
