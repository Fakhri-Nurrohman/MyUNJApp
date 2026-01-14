package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.ConfirmationDialog
import com.fakhrinurrohman.myunjapp.data.Course

@Composable
fun CourseDetailScreen(
    course: Course,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit, // Replaced viewModel with this lambda
    onClose: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(course.color))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = course.userCourseId,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        DetailCourseItem(label = "Teacher", value = course.teacher)
        DetailCourseItem(label = "Room/Location", value = course.room.ifBlank { "Not specified" })
        
        val daysString = remember(course.daysOfWeek) {
            course.daysOfWeek.joinToString(", ") { it.name.lowercase().replaceFirstChar { it.uppercase() } }
        }
        DetailCourseItem(label = "Schedule", value = "$daysString\n${course.startTime} - ${course.endTime}")
        
        DetailCourseItem(
            label = "Frequency", 
            value = when(course.frequencyWeeks) {
                1 -> "Every Week"
                else -> "Every ${course.frequencyWeeks} Weeks"
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Delete")
            }

            Button(
                onClick = { onEditClick(course.id) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Edit")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
    }

    if (showDeleteConfirmation) {
        ConfirmationDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            onConfirm = {
                onDeleteClick(course.id)
                showDeleteConfirmation = false
            },
            title = "Delete Course",
            text = "Are you sure you want to delete '${course.name}'? This action cannot be undone.",
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel"
        )
    }
}

@Composable
fun DetailCourseItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
