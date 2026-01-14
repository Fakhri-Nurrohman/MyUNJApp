package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.fakhrinurrohman.myunjapp.component.ConfirmationDialog
import com.fakhrinurrohman.myunjapp.component.DatePickerModal
import com.fakhrinurrohman.myunjapp.data.Semester
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterAddScreen(
    semester: Semester? = null,
    onSave: (String, LocalDate, LocalDate) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(semester?.name ?: "") }
    var startDate by remember { mutableStateOf(semester?.startDate) }
    var endDate by remember { mutableStateOf(semester?.endDate) }
    
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val hasChanges = remember(name, startDate, endDate) {
        name != (semester?.name ?: "") || 
        startDate != semester?.startDate || 
        endDate != semester?.endDate
    }

    val state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    NavigationBackHandler(
        state = state,
        isBackEnabled = true,
        onBackCompleted = {
            if (hasChanges) {
                showExitDialog = true
            } else {
                onBack()
            }
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Semester Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { showStartDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (startDate == null) "Select Start Date" else "Start: $startDate")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showEndDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (endDate == null) "Select End Date" else "End: $endDate")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (name.isNotBlank() && startDate != null && endDate != null) {
                    onSave(name, startDate!!, endDate!!)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && startDate != null && endDate != null
        ) {
            Text("Save Semester")
        }
    }

    if (showStartDatePicker) {
        DatePickerModal(
            initialDate = startDate,
            onDateSelected = { 
                startDate = it
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerModal(
            initialDate = endDate,
            onDateSelected = { 
                endDate = it
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}
