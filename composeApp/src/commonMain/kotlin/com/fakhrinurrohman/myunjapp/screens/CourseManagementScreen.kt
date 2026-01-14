package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.ConfirmationDialog
import com.fakhrinurrohman.myunjapp.data.Course
import com.fakhrinurrohman.myunjapp.data.Semester
import com.fakhrinurrohman.myunjapp.viewmodels.CourseUiState
import kotlinx.coroutines.delay

@Composable
fun CourseManagementScreen(
    uiState: CourseUiState,
    onSemesterSelected: (Semester) -> Unit,
    onDeleteCourseClick: (String) -> Unit,
    onAddSemesterClick: () -> Unit,
    onAddCourseClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    var courseToDelete by remember { mutableStateOf<String?>(null) }

    var showLoading by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            delay(400)
            showLoading = true
        } else {
            showLoading = false
        }
    }

    if (showLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.semesters.isNotEmpty()) {
            SemesterSelector(
                semesters = uiState.semesters,
                currentSemester = uiState.currentSemester,
                onSemesterSelected = onSemesterSelected
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.semesters.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No semester found.", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "First add your semester before adding courses.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                uiState.courses.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No courses found.", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "Add your first course to get started with this semester.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(uiState.courses) { course ->
                            CourseItem(
                                course = course,
                                onEdit = { onEditClick(course.id) },
                                onDelete = { courseToDelete = course.id }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    if (uiState.semesters.isEmpty()) onAddSemesterClick() else onAddCourseClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (uiState.semesters.isEmpty()) "Add Semester" else "Add Course"
                )
            }
        }
    }

    if (courseToDelete != null) {
        ConfirmationDialog(
            onDismissRequest = { courseToDelete = null },
            onConfirm = {
                courseToDelete?.let { onDeleteCourseClick(it) }
                courseToDelete = null
            },
            title = "Delete Course",
            text = "Are you sure you want to delete this course?",
            confirmButtonText = "Delete"
        )
    }
}

@Composable
fun SemesterSelector(
    semesters: List<Semester>,
    currentSemester: Semester?,
    onSemesterSelected: (Semester) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Semester: ${currentSemester?.name ?: "Select"}",
                    style = MaterialTheme.typography.titleSmall
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            semesters.forEach { semester ->
                DropdownMenuItem(
                    text = { Text(semester.name) },
                    onClick = {
                        onSemesterSelected(semester)
                        expanded = false
                    },
                    trailingIcon = {
                        if (semester.id == currentSemester?.id) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CourseItem(
    course: Course,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .fillMaxHeight()
                    .background(Color(course.color))
            )
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = course.name, style = MaterialTheme.typography.titleMedium)
                    
                    val daysString = remember(course.daysOfWeek) {
                        course.daysOfWeek.joinToString(", ") { it.name.lowercase().replaceFirstChar { it.uppercase() }.take(3) }
                    }
                    
                    Text(
                        text = "$daysString, ${course.startTime} - ${course.endTime}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (course.room.isNotBlank()) {
                        Text(text = "Room: ${course.room}", style = MaterialTheme.typography.labelSmall)
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
