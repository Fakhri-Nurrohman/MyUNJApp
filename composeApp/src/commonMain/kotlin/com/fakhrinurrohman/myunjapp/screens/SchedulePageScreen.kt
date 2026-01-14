package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.RevealWrapper
import com.fakhrinurrohman.myunjapp.data.CalendarEvent
import com.fakhrinurrohman.myunjapp.data.CalendarView
import com.fakhrinurrohman.myunjapp.data.ScheduleUiState
import com.fakhrinurrohman.myunjapp.component.DailyCalendarView
import com.fakhrinurrohman.myunjapp.component.MonthlyCalendarView
import com.fakhrinurrohman.myunjapp.component.WeeklyCalendarView
import com.fakhrinurrohman.myunjapp.component.EmptyState
import com.fakhrinurrohman.myunjapp.component.ScheduleListView
import kotlinx.datetime.*
import kotlinx.coroutines.launch

@Composable
fun SchedulePageScreen(
    uiState: ScheduleUiState,
    onChangeView: (CalendarView) -> Unit,
    onEventClick: (CalendarEvent) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onManageSemestersClick: () -> Unit,
    onManageCoursesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    RevealWrapper(isLoading = uiState.isLoading) {
        Column(modifier = modifier.fillMaxSize()) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                CalendarView.entries.forEachIndexed { index, view ->
                    val selected = uiState.selectedView == view
                    SegmentedButton(
                        selected = selected,
                        onClick = { onChangeView(view) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = CalendarView.entries.size),
                        icon = {},
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = SegmentedButtonDefaults.colors(activeContentColor = Color.Black, inactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    ) {
                        Text(view.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleSmall, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.semester == null) {
                    EmptyState("No Semester Active", "Create a semester to organize your schedule.", Icons.Default.DateRange, "Manage Semesters", onManageSemestersClick)
                } else if (uiState.courses.isEmpty()) {
                    EmptyState("No courses added", "Add your courses to see them on the calendar.", Icons.Default.School, "Add Courses", onManageCoursesClick)
                } else {
                    val semester = uiState.semester
                    when (uiState.selectedView) {
                        CalendarView.SCHEDULE -> ScheduleListView(uiState.allEvents, onEventClick)
                        CalendarView.DAILY -> {
                            val totalDays = semester.startDate.daysUntil(semester.endDate) + 1
                            val initialPage = semester.startDate.daysUntil(uiState.currentDate).coerceIn(0, totalDays - 1)
                            val pagerState = rememberPagerState(initialPage = initialPage) { totalDays }
                            HorizontalPager(state = pagerState) { page ->
                                LaunchedEffect(pagerState.currentPage) {
                                    val newDate = semester.startDate.plus(pagerState.currentPage, DateTimeUnit.DAY)
                                    if (newDate != uiState.currentDate) onDateChanged(newDate)
                                }
                                val date = remember(page) { semester.startDate.plus(page, DateTimeUnit.DAY) }
                                DailyCalendarView(date, uiState.allEvents, { coroutineScope.launch { if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1) } }, { coroutineScope.launch { if (pagerState.currentPage < totalDays - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1) } }, onEventClick)
                            }
                        }
                        CalendarView.WEEKLY -> {
                            val totalWeeks = (semester.startDate.daysUntil(semester.endDate) / 7) + 1
                            val daysToToday = semester.startDate.daysUntil(uiState.currentDate)
                            val initialPage = (daysToToday / 7).coerceIn(0, totalWeeks - 1)
                            val pagerState = rememberPagerState(initialPage = initialPage) { totalWeeks }
                            HorizontalPager(state = pagerState) { page ->
                                LaunchedEffect(pagerState.currentPage) {
                                    val newDate = semester.startDate.plus(pagerState.currentPage * 7, DateTimeUnit.DAY)
                                    if (newDate != uiState.currentDate) onDateChanged(newDate)
                                }
                                val weekStart = remember(page) {
                                    val daysToMonday = semester.startDate.dayOfWeek.ordinal
                                    semester.startDate.plus(-(daysToMonday) + page * 7, DateTimeUnit.DAY)
                                }
                                WeeklyCalendarView(weekStart, uiState.allEvents, { coroutineScope.launch { if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1) } }, { coroutineScope.launch { if (pagerState.currentPage < totalWeeks - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1) } }, { onDateChanged(it); onChangeView(CalendarView.DAILY) }, { onDateChanged(it.start.date); onChangeView(CalendarView.DAILY) })
                            }
                        }
                        CalendarView.MONTHLY -> {
                            val totalMonths = semester.startDate.monthsUntil(semester.endDate) + 1
                            val initialPage = semester.startDate.monthsUntil(uiState.currentDate).coerceIn(0, totalMonths - 1)
                            val pagerState = rememberPagerState(initialPage = initialPage) { totalMonths }
                            HorizontalPager(state = pagerState) { page ->
                                LaunchedEffect(pagerState.currentPage) {
                                    val newDate = semester.startDate.plus(pagerState.currentPage, DateTimeUnit.MONTH)
                                    if (newDate != uiState.currentDate) onDateChanged(newDate)
                                }
                                val monthDate = remember(page) { semester.startDate.plus(page, DateTimeUnit.MONTH) }
                                MonthlyCalendarView(monthDate, uiState.allEvents, { coroutineScope.launch { if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1) } }, { coroutineScope.launch { if (pagerState.currentPage < totalMonths - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1) } }, { onDateChanged(it); onChangeView(CalendarView.DAILY) }, { onDateChanged(it.start.date); onChangeView(CalendarView.DAILY) })
                            }
                        }
                    }
                }
            }
        }
    }
}
