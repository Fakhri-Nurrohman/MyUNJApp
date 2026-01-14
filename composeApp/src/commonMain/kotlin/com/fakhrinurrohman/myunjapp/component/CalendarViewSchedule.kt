package com.fakhrinurrohman.myunjapp.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.data.CalendarEvent
import com.fakhrinurrohman.myunjapp.data.Course
import com.fakhrinurrohman.myunjapp.screens.CourseDashboardItem
import com.fakhrinurrohman.myunjapp.util.toMonthYear
import kotlinx.coroutines.launch

@Composable
fun ScheduleListView(
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit
) {
    if (events.isEmpty()) {
        EmptyState(
            title = "No events scheduled",
            description = "Your agenda is currently empty. Add courses or tasks to see them here.",
            icon = Icons.Default.DateRange
        )
        return
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val groupedByMonth = remember(events) {
        events.groupBy { it.start.date.toMonthYear() }
    }

    val monthIndices = remember(groupedByMonth) {
        val indices = mutableListOf<Int>()
        var currentIndex = 0
        groupedByMonth.forEach { (_, monthEvents) ->
            indices.add(currentIndex)
            currentIndex++
            val groupedByDay = monthEvents.groupBy { it.start.date }
            groupedByDay.forEach { (_, dayEvents) ->
                currentIndex++
                currentIndex += dayEvents.size
            }
        }
        indices
    }

    val currentMonthYear by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val monthIdx = monthIndices.lastOrNull { it <= firstVisibleIndex } ?: monthIndices.first()
            val listPos = monthIndices.indexOf(monthIdx)
            groupedByMonth.keys.elementAt(listPos)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        CalendarHeader(
            title = currentMonthYear,
            onPreviousClick = {
                coroutineScope.launch {
                    val currentIndex = listState.firstVisibleItemIndex
                    val prevTarget = monthIndices.lastOrNull { it < currentIndex } 
                        ?: monthIndices.firstOrNull { it == currentIndex }
                    prevTarget?.let { listState.animateScrollToItem(it) }
                }
            },
            onNextClick = {
                coroutineScope.launch {
                    val currentIndex = listState.firstVisibleItemIndex
                    val nextTarget = monthIndices.firstOrNull { it > currentIndex }
                    nextTarget?.let { listState.animateScrollToItem(it) }
                }
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            groupedByMonth.forEach { (_, monthEvents) ->
                item {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                val groupedByDay = monthEvents.groupBy { it.start.date }
                groupedByDay.forEach { (date, dailyEvents) ->
                    item {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${date.day}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    items(dailyEvents) { event ->
                        Box(modifier = Modifier.padding(start = 44.dp)) {
                            CourseDashboardItem(
                                course = Course(
                                    id = event.courseId ?: event.userEventId ?: "temp_id",
                                    semesterId = event.semesterId ?: "temp_semester",
                                    userCourseId = event.courseId ?: event.title,
                                    name = event.title,
                                    teacher = if (event.sourceCourse != null) "Teacher: ${event.sourceCourse.teacher}" else event.description,
                                    room = if (event.sourceCourse != null) "Room: ${event.sourceCourse.room}" else event.description,
                                    daysOfWeek = emptyList(),
                                    frequencyWeeks = 1,
                                    startTime = event.start.time,
                                    endTime = event.end.time,
                                    color = event.color.toArgb()
                                ),
                                displayDate = date,
                                onClick = { onEventClick(event) }
                            )
                        }
                    }
                }
            }
        }
    }
}
