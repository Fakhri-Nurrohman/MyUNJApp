package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.fakhrinurrohman.myunjapp.component.ColorPickerGrid
import com.fakhrinurrohman.myunjapp.component.ConfirmationDialog
import com.fakhrinurrohman.myunjapp.component.CustomColorPickerDialog
import com.fakhrinurrohman.myunjapp.component.TimePickerModal
import com.fakhrinurrohman.myunjapp.data.Course
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CourseAddScreen(
    course: Course? = null,
    onSave: (userCourseId: String, name: String, teacher: String, room: String, days: Set<DayOfWeek>, freq: Int, start: LocalTime, end: LocalTime, color: Int) -> Unit,
    onBack: () -> Unit
) {
    var userCourseId by remember { mutableStateOf(course?.userCourseId ?: "") }
    var name by remember { mutableStateOf(course?.name ?: "") }
    var teacher by remember { mutableStateOf(course?.teacher ?: "") }
    var room by remember { mutableStateOf(course?.room ?: "") }
    var selectedDays by remember { mutableStateOf(course?.daysOfWeek?.toSet() ?: setOf(DayOfWeek.MONDAY)) }
    var frequencyWeeks by remember { mutableStateOf(course?.frequencyWeeks ?: 1) }
    var startTime by remember { mutableStateOf(course?.startTime ?: LocalTime(9, 0)) }
    var endTime by remember { mutableStateOf(course?.endTime ?: LocalTime(10, 30)) }
    var selectedColor by remember { mutableStateOf(course?.color ?: Color.Gray.toArgb()) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showCustomColorPicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val hasChanges = remember(userCourseId, name, teacher, room, selectedDays, frequencyWeeks, startTime, endTime, selectedColor) {
        userCourseId != (course?.userCourseId ?: "") ||
        name != (course?.name ?: "") ||
        teacher != (course?.teacher ?: "") ||
        room != (course?.room ?: "") ||
        selectedDays != (course?.daysOfWeek?.toSet() ?: setOf(DayOfWeek.MONDAY)) ||
        frequencyWeeks != (course?.frequencyWeeks ?: 1) ||
        startTime != (course?.startTime ?: LocalTime(9, 0)) ||
        endTime != (course?.endTime ?: LocalTime(10, 30)) ||
        selectedColor != (course?.color ?: Color.Gray.toArgb())
    }

    val navState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    NavigationBackHandler(
        state = navState,
        isBackEnabled = true,
        onBackCompleted = { 
            if (hasChanges) showExitDialog = true else onBack() 
        }
    )

    if (showExitDialog) {
        ConfirmationDialog(
            onDismissRequest = { showExitDialog = false },
            onConfirm = {
                showExitDialog = false
                onBack()
            },
            title = "Unsaved Changes",
            text = "You have unsaved changes. Are you sure you want to leave?",
            confirmButtonText = "Discard",
            dismissButtonText = "Keep Editing"
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)
    ) {
        OutlinedTextField(
            value = userCourseId,
            onValueChange = { userCourseId = it },
            label = { Text("Course ID (e.g., CS101)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Course Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = teacher,
            onValueChange = { teacher = it },
            label = { Text("Teacher Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Room / Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Repeat Days", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeek.entries.forEach { day ->
                val isSelected = selectedDays.contains(day)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            selectedDays = if (isSelected) selectedDays - day else selectedDays + day
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.name.take(1),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Frequency", style = MaterialTheme.typography.labelLarge)
        val frequencies = listOf("Weekly" to 1, "2 Weeks" to 2, "3 Weeks" to 3, "Monthly" to 4)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            frequencies.forEachIndexed { index, (label, weeks) ->
                SegmentedButton(
                    selected = frequencyWeeks == weeks,
                    onClick = { frequencyWeeks = weeks },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = frequencies.size)
                ) {
                    Text(label, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { showStartTimePicker = true }, modifier = Modifier.weight(1f)) {
                Text("Start: $startTime")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { showEndTimePicker = true }, modifier = Modifier.weight(1f)) {
                Text("End: $endTime")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Color", style = MaterialTheme.typography.labelLarge)
        ColorPickerGrid(
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it },
            onOpenCustomPicker = { showCustomColorPicker = true }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { 
                onSave(userCourseId, name, teacher, room,
                    selectedDays, frequencyWeeks, startTime, endTime, selectedColor)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && userCourseId.isNotBlank() && selectedDays.isNotEmpty()
        ) {
            Text("Save Course")
        }
    }

    if (showStartTimePicker) {
        TimePickerModal(
            initialTime = startTime,
            onTimeSelected = { startTime = it; showStartTimePicker = false },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        TimePickerModal(
            initialTime = endTime,
            onTimeSelected = { endTime = it; showEndTimePicker = false },
            onDismiss = { showEndTimePicker = false }
        )
    }

    if (showCustomColorPicker) {
        CustomColorPickerDialog(
            initialColor = Color(selectedColor),
            onColorSelected = { 
                selectedColor = it.toArgb()
                showCustomColorPicker = false
            },
            onDismiss = { showCustomColorPicker = false }
        )
    }
}
