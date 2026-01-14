package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.RevealWrapper
import com.fakhrinurrohman.myunjapp.component.SectionHeader
import com.fakhrinurrohman.myunjapp.data.Course
import com.fakhrinurrohman.myunjapp.data.EventType
import com.fakhrinurrohman.myunjapp.data.UserEvent
import com.fakhrinurrohman.myunjapp.util.toRelativeDateString
import com.fakhrinurrohman.myunjapp.viewmodels.HomeUiState
import kotlinx.datetime.*
import kotlin.time.Clock

@Composable
fun HomePageScreen(
    uiState: HomeUiState,
    onCourseClick: (Course) -> Unit = {},
    onTaskClick: (UserEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    RevealWrapper(isLoading = uiState.isLoading) {
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("Welcome Back!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    uiState.currentSemester?.let {
                        Text("Current Semester: ${it.name}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    } ?: Text("No active semester", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard("Courses Today", uiState.todaysCourses.size.toString(), Icons.Default.Class, Modifier.weight(1f))
                    SummaryCard("Pending Tasks", uiState.upcomingTasks.count { !it.isCompleted }.toString(), Icons.AutoMirrored.Default.Assignment, Modifier.weight(1f))
                }
            }

            item { SectionHeader(title = "Today's Schedule", icon = Icons.Default.CalendarMonth) }

            if (uiState.todaysCourses.isEmpty()) {
                item { Text("No classes today. Enjoy your day!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp)) }
            } else {
                items(uiState.todaysCourses) { course ->
                    CourseDashboardItem(course, today, { onCourseClick(course) })
                }
            }

            item { SectionHeader(title = "Upcoming Deadlines", icon = Icons.AutoMirrored.Default.Assignment) }

            if (uiState.upcomingTasks.isEmpty()) {
                item { Text("No upcoming tasks. You're all caught up!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp)) }
            } else {
                items(uiState.upcomingTasks.take(5)) { task ->
                    TaskDashboardItem(task, { onTaskClick(task) })
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SummaryCard(label: String, count: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(count, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun CourseDashboardItem(course: Course, displayDate: LocalDate?, onClick: () -> Unit) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time }
    val isToday = displayDate == null || displayDate == today
    val isOngoing = isToday && now >= course.startTime && now <= course.endTime
    val isFinished = isToday && now > course.endTime
    val courseColor = Color(course.color)
    val cardAlpha = if (isFinished) 0.4f else 1f
    
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = courseColor.copy(alpha = if (isOngoing) 0.2f else 0.1f * cardAlpha))) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(if (isFinished) Color.Gray else courseColor))
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(course.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (isFinished) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface)
                    Text("${course.startTime} - ${course.endTime}", style = MaterialTheme.typography.bodySmall, color = if (isFinished) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(course.room, style = MaterialTheme.typography.bodySmall, color = if (isFinished) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isOngoing) {
                    Surface(color = MaterialTheme.colorScheme.error, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)) {
                        Text("LIVE", Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDashboardItem(task: UserEvent, onClick: () -> Unit) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val isOverdue = task.date < today && !task.isCompleted
    val isDueToday = task.date == today && !task.isCompleted
    val indicatorColor = if (isOverdue) MaterialTheme.colorScheme.error else {
        task.color?.let { Color(it) } ?: when(task.type) {
            EventType.EXAM -> Color(0xFFE57373)
            EventType.HOMEWORK -> Color(0xFF64B5F6)
            else -> Color.Gray
        }
    }
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = indicatorColor.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(indicatorColor))
            Row(modifier = Modifier.padding(16.dp).weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(task.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    Text("Due: ${task.date.toRelativeDateString()}", style = MaterialTheme.typography.bodySmall, color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isOverdue) { Icon(Icons.Default.Warning, "Overdue", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)) }
                else if (isDueToday) {
                    Surface(color = MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)) {
                        Text("TODAY", Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
