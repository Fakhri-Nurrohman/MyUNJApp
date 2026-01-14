package com.fakhrinurrohman.myunjapp.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fakhrinurrohman.myunjapp.data.CalendarEvent
import com.fakhrinurrohman.myunjapp.util.toMonthYear
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Composable
fun MonthlyCalendarView(
    monthDate: LocalDate, 
    events: List<CalendarEvent>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (CalendarEvent) -> Unit
) {
    val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val firstDayOfMonth = LocalDate(monthDate.year, monthDate.month, 1)
    val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).plus(-1, DateTimeUnit.DAY)
    val daysInMonth = lastDayOfMonth.day
    val startPadding = firstDayOfMonth.dayOfWeek.ordinal // 0 for Mon
    
    val totalSlots = daysInMonth + startPadding
    val rows = (totalSlots + 6) / 7

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        CalendarHeader(
            title = remember(monthDate) { monthDate.toMonthYear() },
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day.uppercase(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until rows) {
                Row(modifier = Modifier.weight(1f)) {
                    for (col in 0 until 7) {
                        val dayIndex = row * 7 + col - startPadding
                        if (dayIndex in 0 until daysInMonth) {
                            val dayNumber = dayIndex + 1
                            val currentLocalDate = LocalDate(monthDate.year, monthDate.month, dayNumber)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                                    .clickable { onDayClick(currentLocalDate) },
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayNumber.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    
                                    val dayEvents = events.filter { currentLocalDate >= it.start.date && currentLocalDate <= it.end.date }
                                    
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                                        verticalArrangement = Arrangement.spacedBy(1.dp)
                                    ) {
                                        dayEvents.take(3).forEach { event ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(18.dp)
                                                    .background(event.color.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                                                    .clickable { onEventClick(event) },
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    text = event.title,
                                                    fontSize = 8.sp,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.padding(horizontal = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }
        }
    }
}
