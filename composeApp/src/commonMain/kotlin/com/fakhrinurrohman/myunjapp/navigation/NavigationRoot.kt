package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavKey
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation3.ui.NavDisplay
import com.fakhrinurrohman.myunjapp.scenes.rememberListDetailSceneStrategy
import com.fakhrinurrohman.myunjapp.viewmodels.AuthViewModel
import com.fakhrinurrohman.myunjapp.viewmodels.ScheduleViewModel

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val topLevelDestinations = getTopLevelDestinations()
    val navigationState = rememberNavigationState(
        startRoute = Route.HomePage,
        topLevelRoutes = topLevelDestinations.keys
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val navStoreResult = rememberNavigationStoreResult()

    val scheduleViewModel: ScheduleViewModel = koinViewModel()
    val authViewModel: AuthViewModel = koinViewModel()
    
    val uiState by scheduleViewModel.uiState.collectAsState()
    val isSyncing by authViewModel.isSyncing.collectAsState()

    val currentBackStack = navigationState.backStacks[navigationState.topLevelRoute]
    val currentRoute = currentBackStack?.lastOrNull() ?: navigationState.topLevelRoute

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                selectedKey = navigationState.topLevelRoute,
                titleOverride = getRouteTitle(currentRoute, uiState.semester?.name),
                navigationIcon = {
                    if (!topLevelDestinations.containsKey(currentRoute)) {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                actions = {
                    val isMainPage = currentRoute == Route.SchedulePage || 
                                   currentRoute == Route.TasksPage || 
                                   currentRoute == Route.HomePage
                    
                    if (isMainPage && !uiState.isLoading) {
                        ScheduleActions(
                            onManageCourses = { navigator.navigate(Route.ManageCourse) },
                            onManageSemesters = { navigator.navigate(Route.ManageSemester) }
                        )
                    }

                    // Smart Sync Action
                    if (currentRoute == Route.ManageSemester || currentRoute == Route.ManageCourse) {
                        IconButton(
                            onClick = { 
                                authViewModel.handleSyncAction(
                                    onLoginRequired = { navigator.navigate(Route.Login) }
                                )
                            },
                            enabled = !isSyncing
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sync from SIAKAD",
                                tint = if (isSyncing) Color.LightGray else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedKey = navigationState.topLevelRoute, onSelectedKey = { navigator.navigate(it) })
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color.White),
            onBack = navigator::goBack,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            sceneStrategy = rememberListDetailSceneStrategy(),
            entries = navigationState.toEntries(
                entryProvider = createNavEntries(navigator, navStoreResult)
            )
        )
    }
}

private fun getRouteTitle(route: NavKey, semesterName: String?): String? = when (route) {
    Route.SchedulePage -> semesterName
    Route.ManageSemester -> "Manage Semesters"
    Route.AddSemester -> "Add Semester"
    is Route.EditSemester -> "Edit Semester"
    Route.ManageCourse -> "Manage Courses"
    Route.AddCourse -> "Add Course"
    is Route.EditCourse -> "Edit Course"
    is Route.DetailCourse -> "Course Details"
    is Route.DetailUserEvent -> "Event Details"
    Route.AddUserEvent -> "Add Task/Event"
    Route.InformationPage -> "Information"
    is Route.NewsList -> route.title
    is Route.CampusList -> route.title
    is Route.BuildingList -> route.title
    is Route.RoomList -> route.title
    is Route.FacultyList -> route.title
    is Route.StudyProgramList -> route.title
    is Route.LecturerList -> route.title
    is Route.LecturerDetail -> route.title
    is Route.WebView -> route.title
    Route.Login -> "SIAKAD Login"
    else -> null
}

@Composable
private fun ScheduleActions(onManageCourses: () -> Unit, onManageSemesters: () -> Unit) {
    IconButton(onClick = onManageCourses) {
        Icon(
            imageVector = Icons.Filled.School, 
            contentDescription = "Courses", 
            tint = MaterialTheme.colorScheme.secondary
        )
    }
    IconButton(onClick = onManageSemesters) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List, 
            contentDescription = "Semesters", 
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
