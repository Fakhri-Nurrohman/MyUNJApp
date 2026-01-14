package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.ConfirmationDialog
import com.fakhrinurrohman.myunjapp.data.Semester
import com.fakhrinurrohman.myunjapp.viewmodels.SemesterUiState
import kotlinx.coroutines.delay

@Composable
fun SemesterManagementScreen(
    uiState: SemesterUiState,
    onSelectSemester: (Semester) -> Unit,
    onDeleteSemester: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    var semesterToDelete by remember { mutableStateOf<String?>(null) }
    
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

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.semesters.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No semesters found.")
                Text("Add your first semester to get started.", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(uiState.semesters) { semester ->
                    SemesterItem(
                        semester = semester,
                        isSelected = semester.id == uiState.currentSemester?.id,
                        onSelect = { onSelectSemester(semester) },
                        onEdit = { onEditClick(semester.id) },
                        onDelete = { semesterToDelete = semester.id }
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Semester")
        }
    }

    if (semesterToDelete != null) {
        ConfirmationDialog(
            onDismissRequest = { semesterToDelete = null },
            onConfirm = {
                semesterToDelete?.let { onDeleteSemester(it) }
                semesterToDelete = null
            },
            title = "Delete Semester",
            text = "Are you sure you want to delete this semester? This action cannot be undone.",
            confirmButtonText = "Delete"
        )
    }
}

@Composable
fun SemesterItem(
    semester: Semester,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateText = remember(semester.startDate, semester.endDate) {
        val startDay = semester.startDate.day
        val startMonth = semester.startDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val startYear = semester.startDate.year.toString().takeLast(2)
        
        val endDay = semester.endDate.day
        val endMonth = semester.endDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val endYear = semester.endDate.year.toString().takeLast(2)
        
        "$startDay $startMonth $startYear - $endDay $endMonth $endYear"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect() },
        colors = cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = semester.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall
                )
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
