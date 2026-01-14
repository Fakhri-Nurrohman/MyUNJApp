package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.data.UserEvent
import com.fakhrinurrohman.myunjapp.data.ScheduleUiState
import com.fakhrinurrohman.myunjapp.component.EmptyState
import com.fakhrinurrohman.myunjapp.component.RevealWrapper
import com.fakhrinurrohman.myunjapp.util.toReadableDate

@Composable
fun TasksPageScreen(
    uiState: ScheduleUiState,
    onToggleCompletion: (String, Boolean) -> Unit,
    onTaskClick: (UserEvent) -> Unit,
    onAddTaskClick: () -> Unit,
    onManageSemestersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks = uiState.allEvents
        .filter { it.sourceEvent != null }
        .mapNotNull { it.sourceEvent }
        .distinctBy { it.id }

    RevealWrapper(isLoading = uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize()) {
            when {
                uiState.semester == null -> {
                    EmptyState(
                        title = "No Semester Active",
                        description = "Create a semester first to start adding tasks and homework.",
                        icon = Icons.Default.DateRange,
                        buttonText = "Manage Semesters",
                        onButtonClick = onManageSemestersClick
                    )
                }
                tasks.isEmpty() -> {
                    EmptyState(
                        title = "No tasks found",
                        description = "You haven't added any tasks or homework yet. Tap the button below to stay organized!",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        buttonText = "Add First Task",
                        onButtonClick = onAddTaskClick
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                onToggle = { onToggleCompletion(task.id, it) },
                                onClick = { onTaskClick(task) }
                            )
                        }
                    }
                }
            }

            if (uiState.semester != null) {
                FloatingActionButton(
                    onClick = onAddTaskClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: UserEvent,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor = task.color?.let { Color(it).copy(alpha = 0.1f) } ?: MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val indicatorColor = task.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Side Indicator
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )
            Row(
                modifier = Modifier.padding(16.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onToggle,
                    colors = CheckboxDefaults.colors(checkedColor = indicatorColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Due: ${task.date.toReadableDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
