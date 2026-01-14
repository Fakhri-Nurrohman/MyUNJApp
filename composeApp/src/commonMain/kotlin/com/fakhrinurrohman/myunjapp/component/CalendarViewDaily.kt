package com.fakhrinurrohman.myunjapp.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.data.CalendarEvent
import com.fakhrinurrohman.myunjapp.util.toFullReadableDate
import kotlinx.datetime.LocalDate

@Composable
fun DailyCalendarView(
    date: LocalDate, 
    events: List<CalendarEvent>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onEventClick: (CalendarEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val hourHeight = 80.dp
    val hours = (0..23).toList()

    val dayEvents = remember(events, date) {
        events.filter { date >= it.start.date && date <= it.end.date }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        CalendarHeader(
            title = remember(date) { date.toFullReadableDate() },
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick
        )

        Box(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
            // Background Grid Layer
            Column(modifier = Modifier.padding(top = 12.dp)) {
                hours.forEach { hour ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(hourHeight),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${hour.toString().padStart(2, '0')}:00",
                            modifier = Modifier.width(48.dp).offset(y = (-10).dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 4.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }

            // Foreground Events Layer
            dayEvents.forEach { event ->
                val startInMinutes = if (date == event.start.date) event.start.hour * 60 + event.start.minute else 0
                val endInMinutes = if (date == event.end.date) event.end.hour * 60 + event.end.minute else 24 * 60
                
                val gridTopPadding = 12.dp
                val topOffset = ((startInMinutes / 60f) * hourHeight.value).dp + gridTopPadding
                val durationInHours = (endInMinutes - startInMinutes) / 60f
                val eventHeight = durationInHours * hourHeight.value

                val overlappingEvents = dayEvents.filter { other ->
                    val otherStart = if (date == other.start.date) other.start.hour * 60 + other.start.minute else 0
                    val otherEnd = if (date == other.end.date) other.end.hour * 60 + other.end.minute else 24 * 60
                    (startInMinutes < otherEnd && endInMinutes > otherStart)
                }
                
                val columnCount = overlappingEvents.size
                val columnIndex = overlappingEvents.indexOf(event)

                if (eventHeight > 0) {
                    Card(
                        modifier = Modifier
                            .padding(start = 56.dp, end = 8.dp)
                            .offset(y = topOffset)
                            .fillMaxWidth(fraction = 1f / columnCount)
                            .offset(x = (columnIndex * (1f / columnCount) * 300).dp)
                            .height(eventHeight.dp)
                            .clickable { onEventClick(event) },
                        colors = CardDefaults.cardColors(containerColor = event.color),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                            )
                            if (eventHeight > 40) {
                                Text(
                                    text = if (event.sourceCourse != null) "Room: ${event.sourceCourse.room}" else event.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
