package com.fakhrinurrohman.myunjapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {
    // Top Level Routes
    @Serializable data object HomePage: Route
    @Serializable data object TasksPage: Route
    @Serializable data object SchedulePage: Route
    @Serializable data object InformationPage : Route
    @Serializable data object MorePage: Route

    // Auth Screens
    @Serializable data object Login : Route

    // Setting Screens
    @Serializable data object SettingScreenChange: Route

    // --- Semester Screens ---
    @Serializable data object ManageSemester : Route
    @Serializable data object AddSemester : Route
    @Serializable data class EditSemester(val semesterId: String) : Route

    // --- Course Screens ---
    @Serializable data object ManageCourse : Route
    @Serializable data object AddCourse : Route
    @Serializable data class EditCourse(val courseId: String) : Route
    @Serializable data class DetailCourse(val courseId: String) : Route

    // --- User Event Screens ---
    @Serializable data class DetailUserEvent(val eventId: String) : Route
    @Serializable data object AddUserEvent : Route
    @Serializable data class EditUserEvent(val eventId: String) : Route

    // --- WebView ---
    @Serializable data class WebView(val url: String, val title: String) : Route
    
    // --- Specific Information Routes ---
    @Serializable data class NewsList(val title: String) : Route
    @Serializable data class CampusList(val title: String) : Route
    @Serializable data class BuildingList(val campusId: String, val title: String) : Route
    @Serializable data class RoomList(val buildingId: String, val title: String) : Route
    @Serializable data class FacultyList(val title: String) : Route
    @Serializable data class StudyProgramList(val facultyId: String, val title: String) : Route
    @Serializable data class LecturerList(val programId: String, val title: String) : Route
    @Serializable data class LecturerDetail(val lecturerId: String, val title: String) : Route
}
