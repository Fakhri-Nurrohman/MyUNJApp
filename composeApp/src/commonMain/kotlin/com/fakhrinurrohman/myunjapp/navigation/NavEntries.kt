package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.runtime.*
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import org.koin.compose.viewmodel.koinViewModel
import com.fakhrinurrohman.myunjapp.data.EventType
import com.fakhrinurrohman.myunjapp.scenes.ListDetailScene
import com.fakhrinurrohman.myunjapp.screens.*
import com.fakhrinurrohman.myunjapp.viewmodels.*

/**
 * Defines which screen to show for each route in the application.
 */
@Composable
fun createNavEntries(
    navigator: Navigator,
    navStoreResult: NavigationStoreResult
): (NavKey) -> NavEntry<NavKey> {
    return entryProvider<NavKey> {
        entry<Route.HomePage> {
            val viewModel: HomeViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            HomePageScreen(
                uiState = uiState,
                onCourseClick = { course -> navigator.navigate(Route.DetailCourse(course.id)) },
                onTaskClick = { event -> navigator.navigate(Route.DetailUserEvent(event.id)) }
            )
        }

        entry<Route.TasksPage> {
            val scheduleViewModel: ScheduleViewModel = koinViewModel()
            val userEventViewModel: UserEventViewModel = koinViewModel()
            val scheduleUiState by scheduleViewModel.uiState.collectAsState()
            
            TasksPageScreen(
                uiState = scheduleUiState,
                onToggleCompletion = { id, isCompleted ->
                    userEventViewModel.toggleTaskCompletion(id, isCompleted)
                },
                onTaskClick = { event ->
                    navigator.navigate(Route.DetailUserEvent(event.id))
                },
                onAddTaskClick = { navigator.navigate(Route.AddUserEvent) },
                onManageSemestersClick = { navigator.navigate(Route.ManageSemester) }
            )
        }

        entry<Route.SchedulePage>(metadata = ListDetailScene.listPane()) {
            val scheduleViewModel: ScheduleViewModel = koinViewModel()
            val uiState by scheduleViewModel.uiState.collectAsState()
            SchedulePageScreen(
                uiState = uiState,
                onChangeView = { scheduleViewModel.changeView(it) },
                onEventClick = { event ->
                    if (event.type == EventType.COURSE) {
                        event.courseId?.let { navigator.navigate(Route.DetailCourse(it)) }
                    } else {
                        event.userEventId?.let { navigator.navigate(Route.DetailUserEvent(it)) }
                    }
                },
                onDateChanged = { scheduleViewModel.updateCurrentDate(it) },
                onManageSemestersClick = { navigator.navigate(Route.ManageSemester) },
                onManageCoursesClick = { navigator.navigate(Route.ManageCourse) }
            )
        }

        // --- Information Screens ---
        entry<Route.InformationPage> {
            val viewModel: InformationViewModel = koinViewModel()
            InformationPageScreen(
                viewModel = viewModel,
                onNavigateToUrl = { url, title -> navigator.navigate(Route.WebView(url, title)) },
                onNavigateToNews = { navigator.navigate(Route.NewsList("University News")) },
                onNavigateToCampuses = { navigator.navigate(Route.CampusList("Campuses")) },
                onNavigateToFaculties = { navigator.navigate(Route.FacultyList("Faculties")) }
            )
        }

        entry<Route.NewsList> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            DetailInformationScreen(
                subtitle = "University News",
                listItems = uiState.news.map { it.title },
                onItemClick = { index ->
                    val newsItem = uiState.news[index]
                    navigator.navigate(Route.WebView(newsItem.url, newsItem.title))
                },
                filterCategories = listOf("Semua", "Terbaru", "Prestasi", "Riset & Inovasi", "Pengabdian"),
                selectedCategory = uiState.selectedNewsCategory,
                onCategorySelected = { viewModel.selectNewsCategory(it) }
            )
        }

        entry<Route.CampusList> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            DetailInformationScreen(
                subtitle = "Lokasi Kampus UNJ",
                listItems = uiState.campuses.map { it.name },
                onItemClick = { index ->
                    val campus = uiState.campuses[index]
                    navigator.navigate(Route.BuildingList(campus.id, campus.name))
                },
            )
        }

        entry<Route.BuildingList> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val campus = uiState.campuses.find { it.id == key.campusId }
            val buildings by viewModel.getBuildings(key.campusId).collectAsState(emptyList())
            
            DetailInformationScreen(
                subtitle = "Daftar Gedung",
                description = campus?.description,
                coordinates = if (campus?.latitude != null && campus.longitude != null) {
                    Pair(campus.latitude, campus.longitude)
                } else null,
                locationTitle = campus?.name,
                listItems = buildings.map { it.name },
                onItemClick = { index ->
                    val building = buildings[index]
                    navigator.navigate(Route.RoomList(building.id, building.name))
                },
            )
        }

        entry<Route.RoomList> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            var building by remember { mutableStateOf<com.fakhrinurrohman.myunjapp.data.Building?>(null) }
            val rooms by viewModel.getRooms(key.buildingId).collectAsState(emptyList())
            
            LaunchedEffect(key.buildingId) {
                building = viewModel.getBuildingById(key.buildingId)
            }
            
            DetailInformationScreen(
                subtitle = "Daftar Ruangan",
                description = building?.description,
                coordinates = if (building?.latitude != null && building?.longitude != null) {
                    Pair(building!!.latitude!!, building!!.longitude!!)
                } else null,
                locationTitle = building?.name,
                listItems = rooms.map { "${it.name} (${it.floor ?: ""})" },
            )
        }

        entry<Route.FacultyList> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            DetailInformationScreen(
                subtitle = "Daftar Fakultas",
                listItems = uiState.faculties.map { it.name },
                onItemClick = { index ->
                    val faculty = uiState.faculties[index]
                    navigator.navigate(Route.StudyProgramList(faculty.id, faculty.name))
                },
            )
        }

        entry<Route.StudyProgramList> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            val programs by viewModel.getStudyPrograms(key.facultyId).collectAsState(emptyList())
            DetailInformationScreen(
                subtitle = "Program Studi",
                listItems = programs.map { it.name },
                onItemClick = { index ->
                    val program = programs[index]
                    navigator.navigate(Route.LecturerList(program.id, program.name))
                },
            )
        }

        entry<Route.LecturerList> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            val lecturers by viewModel.getLecturersByProgram(key.programId).collectAsState(emptyList())
            DetailInformationScreen(
                subtitle = "Daftar Dosen",
                listItems = lecturers.map { it.name },
                onItemClick = { index ->
                    val lecturer = lecturers[index]
                    navigator.navigate(Route.LecturerDetail(lecturer.id, lecturer.name))
                },
            )
        }

        entry<Route.LecturerDetail> { key ->
            val viewModel: InformationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            uiState.lecturers.find { it.id == key.lecturerId }?.let { lecturer ->
                DetailInformationScreen(
                    subtitle = "Profil Dosen",
                    description = lecturer.expertise?.let { "Expertise: $it" },
                    extraInfo = mapOf(
                        "NIP" to (lecturer.nip ?: "-"),
                        "Email" to (lecturer.email ?: "-")
                    ),
                )
            }
        }

        entry<Route.Login> {
            val authViewModel: AuthViewModel = koinViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navigator.goBack() },
                onBack = { navigator.goBack() }
            )
        }

        entry<Route.MorePage> {
            MoreScreen(
                navigationStoreResult = navStoreResult, 
                onChangeSettingClick = { navigator.navigate(Route.SettingScreenChange) },
                onNavigateToUrl = { url, title -> navigator.navigate(Route.WebView(url, title)) }
            )
        }
        entry<Route.SettingScreenChange> {
            SettingScreenChange(navStoreResult, onSave = { navigator.goBack() })
        }

        // --- Semester Screens ---
        entry<Route.ManageSemester> {
            val semesterViewModel: SemesterViewModel = koinViewModel()
            val uiState by semesterViewModel.uiState.collectAsState()
            
            SemesterManagementScreen(
                uiState = uiState,
                onSelectSemester = { semesterViewModel.selectSemester(it) },
                onDeleteSemester = { semesterViewModel.deleteSemester(it) },
                onAddClick = { navigator.navigate(Route.AddSemester) },
                onEditClick = { id -> navigator.navigate(Route.EditSemester(id)) }
            )
        }
        entry<Route.AddSemester> {
            val semesterViewModel: SemesterViewModel = koinViewModel()
            SemesterAddScreen(
                onSave = { name, start, end ->
                    semesterViewModel.addSemester(name, start, end)
                    navigator.goBack()
                },
                onBack = { navigator.goBack() }
            )
        }
        entry<Route.EditSemester> { key ->
            val semesterViewModel: SemesterViewModel = koinViewModel()
            val uiState by semesterViewModel.uiState.collectAsState()
            uiState.semesters.find { it.id == key.semesterId }?.let { foundSemester ->
                SemesterAddScreen(
                    semester = foundSemester,
                    onSave = { name, start, end ->
                        semesterViewModel.updateSemester(foundSemester.id, name, start, end)
                        navigator.goBack()
                    },
                    onBack = { navigator.goBack() }
                )
            }
        }

        // --- Course Screens ---
        entry<Route.ManageCourse> {
            val courseViewModel: CourseViewModel = koinViewModel()
            val semesterViewModel: SemesterViewModel = koinViewModel()
            val uiState by courseViewModel.uiState.collectAsState()
            
            CourseManagementScreen(
                uiState = uiState,
                onSemesterSelected = { semesterViewModel.selectSemester(it) },
                onDeleteCourseClick = { courseViewModel.deleteCourse(it) },
                onAddSemesterClick = { navigator.navigate(Route.AddSemester) },
                onAddCourseClick = { navigator.navigate(Route.AddCourse) },
                onEditClick = { id -> navigator.navigate(Route.EditCourse(id)) }
            )
        }
        entry<Route.AddCourse> {
            val courseViewModel: CourseViewModel = koinViewModel()
            CourseAddScreen(
                onSave = { userCourseId, name, teacher, room, days, freq, start, end, color ->
                    courseViewModel.addCourse(userCourseId, name, teacher, room, days, freq, start, end, color)
                    navigator.goBack()
                },
                onBack = { navigator.goBack() }
            )
        }
        entry<Route.EditCourse> { key ->
            val courseViewModel: CourseViewModel = koinViewModel()
            val uiState by courseViewModel.uiState.collectAsState()
            uiState.courses.find { it.id == key.courseId }?.let { foundCourse ->
                CourseAddScreen(
                    course = foundCourse,
                    onSave = { userCourseId, name, teacher, room, days, freq, start, end, color ->
                        courseViewModel.updateCourse(foundCourse.id, userCourseId, name, teacher, room, days, freq, start, end, color)
                        navigator.goBack()
                    },
                    onBack = { navigator.goBack() }
                )
            }
        }
        entry<Route.DetailCourse> { key ->
            val courseViewModel: CourseViewModel = koinViewModel()
            val uiState by courseViewModel.uiState.collectAsState()
            uiState.courses.find { it.id == key.courseId }?.let { foundCourse ->
                CourseDetailScreen(
                    course = foundCourse,
                    onEditClick = { id -> navigator.navigate(Route.EditCourse(id)) },
                    onDeleteClick = { id -> 
                        courseViewModel.deleteCourse(id)
                        navigator.goBack()
                    },
                    onClose = { navigator.goBack() }
                )
            }
        }

        // --- User Event Screens ---
        entry<Route.AddUserEvent> {
            val userEventViewModel: UserEventViewModel = koinViewModel()
            val courseViewModel: CourseViewModel = koinViewModel()
            val uiState by userEventViewModel.uiState.collectAsState()
            val courseUiState by courseViewModel.uiState.collectAsState()
            
            UserEventAddScreen(
                semester = uiState.currentSemester,
                courses = courseUiState.courses,
                onSave = { title, desc, type, date, start, end, courseId, color ->
                    userEventViewModel.addUserEvent(title, desc, type, date, start, end, courseId, color)
                    navigator.goBack()
                },
                onBack = { navigator.goBack() }
            )
        }
        entry<Route.EditUserEvent> { key ->
            val userEventViewModel: UserEventViewModel = koinViewModel()
            val courseViewModel: CourseViewModel = koinViewModel()
            val uiState by userEventViewModel.uiState.collectAsState()
            val courseUiState by courseViewModel.uiState.collectAsState()
            
            uiState.userEvents.find { it.id == key.eventId }?.let { foundEvent ->
                UserEventAddScreen(
                    event = foundEvent,
                    semester = uiState.currentSemester,
                    courses = courseUiState.courses,
                    onSave = { title, desc, type, date, start, end, courseId, color ->
                        userEventViewModel.updateUserEvent(foundEvent.id, title, desc, type, date, start, end, courseId, foundEvent.isCompleted, color)
                        navigator.goBack()
                    },
                    onBack = { navigator.goBack() }
                )
            }
        }
        entry<Route.DetailUserEvent> { key ->
            val userEventViewModel: UserEventViewModel = koinViewModel()
            val uiState by userEventViewModel.uiState.collectAsState()
            uiState.userEvents.find { it.id == key.eventId }?.let { foundEvent ->
                UserEventDetailScreen(
                    event = foundEvent,
                    onDeleteClick = { id ->
                        userEventViewModel.deleteUserEvent(id)
                        navigator.goBack()
                    },
                    onEditClick = { id -> navigator.navigate(Route.EditUserEvent(id)) },
                    onToggleCompletion = { id, completed ->
                        userEventViewModel.toggleTaskCompletion(id, completed)
                    },
                    onClose = { navigator.goBack() }
                )
            }
        }

        // --- Information Screens ---
        entry<Route.WebView> { key ->
            WebViewScreen(
                url = key.url,
                title = key.title,
                onBack = { navigator.goBack() }
            )
        }
    }
}
