package com.fakhrinurrohman.myunjapp.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fakhrinurrohman.myunjapp.data.CalendarEvent
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Composable
fun WeeklyCalendarView(
    startDate: LocalDate, 
    events: List<CalendarEvent>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (CalendarEvent) -> Unit
) {
    val hourHeight = 64.dp
    val hours = (0..23).toList()
    val days = remember(startDate) { (0..6).map { startDate.plus(it, DateTimeUnit.DAY) } }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        CalendarHeader(
            title = remember(startDate) {
                val monthName = startDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
                "$monthName ${startDate.year}"
            },
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick
        )

        Row( modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp)
        ) {
            days.forEach { day ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDayClick(day) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(day.dayOfWeek.name.take(3), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(day.day.toString(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = 24.dp),
        ) {
            Column {
                hours.forEach { hour ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(hourHeight),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${hour.toString().padStart(2, '0')}:00",
                            modifier = Modifier.width(48.dp).offset(y = (-8).dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxSize().padding(start = 48.dp)) {
                days.forEach { day ->
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onDayClick(day) }
                    ) {
                        val dayEvents = remember(events, day) {
                            events.filter { day >= it.start.date && day <= it.end.date }
                        }
                        
                        dayEvents.forEach { event ->
                            val startInMinutes = if (day == event.start.date) event.start.hour * 60 + event.start.minute else 0
                            val endInMinutes = if (day == event.end.date) event.end.hour * 60 + event.end.minute else 24 * 60
                            
                            val topOffset = (startInMinutes / 60f) * hourHeight.value
                            val durationInHours = (endInMinutes - startInMinutes) / 60f
                            val eventHeight = durationInHours * hourHeight.value

                            val overlappingEvents = dayEvents.filter { other ->
                                val otherStart = if (day == other.start.date) other.start.hour * 60 + other.start.minute else 0
                                val otherEnd = if (day == other.end.date) other.end.hour * 60 + other.end.minute else 24 * 60
                                (startInMinutes < otherEnd && endInMinutes > otherStart)
                            }
                            
                            val columnCount = overlappingEvents.size
                            val columnIndex = overlappingEvents.indexOf(event)

                            if (eventHeight > 0) {
                                Card(
                                    modifier = Modifier
                                        .offset(y = topOffset.dp)
                                        .fillMaxWidth(1f / columnCount)
                                        .offset(x = (columnIndex * (1f / columnCount) * 40).dp)
                                        .height(eventHeight.dp)
                                        .padding(horizontal = 0.5.dp)
                                        .clickable { onEventClick(event) },
                                    colors = CardDefaults.cardColors(containerColor = event.color),
                                    shape = RoundedCornerShape(2.dp)
                                ) {
                                    if (eventHeight > 15) {
                                        Text(
                                            text = event.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = (10 / columnCount).coerceAtLeast(6).sp,
                                            maxLines = 1,
                                            modifier = Modifier.padding(1.dp),
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
