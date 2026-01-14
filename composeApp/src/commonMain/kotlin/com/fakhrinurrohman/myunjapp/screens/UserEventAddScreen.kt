package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.fakhrinurrohman.myunjapp.component.*
import com.fakhrinurrohman.myunjapp.data.*
import kotlinx.datetime.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UserEventAddScreen(
    event: UserEvent? = null,
    semester: Semester?, // Required for date range validation
    courses: List<Course>,
    onSave: (title: String, desc: String, type: EventType, date: LocalDate, start: LocalTime?, end: LocalTime?, courseId: String?, color: Int?) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var selectedType by remember { mutableStateOf(event?.type ?: EventType.HOMEWORK) }
    var selectedDate by remember(event?.date) {
        mutableStateOf(event?.date ?: kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    var startTime by remember { mutableStateOf(event?.startTime) }
    var endTime by remember { mutableStateOf(event?.endTime) }
    var selectedCourseId by remember { mutableStateOf(event?.courseId) }
    var selectedColor by remember { mutableStateOf(event?.color ?: Color.Gray.toArgb()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showCourseDropdown by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showCustomColorPicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val hasChanges = remember(title, description, selectedType, selectedDate, startTime, endTime, selectedCourseId, selectedColor) {
        title != (event?.title ?: "") ||
        description != (event?.description ?: "") ||
        selectedType != (event?.type ?: EventType.HOMEWORK) ||
        selectedDate != (event?.date ?: kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())) ||
        startTime != event?.startTime ||
        endTime != event?.endTime ||
        selectedCourseId != event?.courseId ||
        selectedColor != (event?.color ?: Color.Gray.toArgb())
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Event Type", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val types = listOf(EventType.HOMEWORK, EventType.EXAM, EventType.CUSTOM)
                types.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size)
                    ) {
                        Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Date: $selectedDate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { showStartTimePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(if (startTime == null) "Set Start Time" else "Start: $startTime")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = { showEndTimePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(if (endTime == null) "Set End Time" else "End: $endTime")
                }
            }
            
            if (startTime != null) {
                TextButton(onClick = { startTime = null; endTime = null }) {
                    Text("Clear Time")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedType != EventType.CUSTOM) {
                Text("Link to Course", style = MaterialTheme.typography.labelLarge)
                Box {
                    OutlinedCard(
                        onClick = { showCourseDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val courseName = courses.find { it.id == selectedCourseId }?.name ?: "None"
                            Text(text = "Course: $courseName")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(expanded = showCourseDropdown, onDismissRequest = { showCourseDropdown = false }) {
                        DropdownMenuItem(text = { Text("None") }, onClick = { selectedCourseId = null; showCourseDropdown = false })
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.name) },
                                onClick = { selectedCourseId = course.id; showCourseDropdown = false }
                            )
                        }
                    }
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
                    // VALIDATION LOGIC
                    if (semester != null && (selectedDate < semester.startDate || selectedDate > semester.endDate)) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Error: Date must be between ${semester.startDate} and ${semester.endDate}",
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        onSave(title, description, selectedType, selectedDate, startTime, endTime, selectedCourseId, selectedColor) 
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Save ${selectedType.name.lowercase().replaceFirstChar { it.uppercase() }}")
            }
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            initialDate = selectedDate,
            onDateSelected = { it?.let { selectedDate = it }; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showStartTimePicker) {
        TimePickerModal(
            initialTime = startTime ?: LocalTime(9, 0),
            onTimeSelected = { startTime = it; showStartTimePicker = false },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        TimePickerModal(
            initialTime = endTime ?: LocalTime(10, 0),
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
