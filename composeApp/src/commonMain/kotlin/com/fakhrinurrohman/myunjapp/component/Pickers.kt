package com.fakhrinurrohman.myunjapp.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import kotlin.time.Instant

/**
 * Standard palette for courses and events
 */
val EVENT_COLORS = listOf(
    Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), Color(0xFF9575CD),
    Color(0xFF7986CB), Color(0xFF64B5F6), Color(0xFF4FC3F7), Color(0xFF4DB6AC),
    Color(0xFF81C784), Color(0xFFDCE775), Color(0xFFFFF176), Color(0xFFFFB74D),
    Color(0xFF000000)
)

/**
 * Reusable Color Picker Grid with Custom Color support
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerGrid(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    onOpenCustomPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EVENT_COLORS.forEach { color ->
            val argb = color.toArgb()
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selectedColor == argb) 3.dp else 0.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(argb) }
            )
        }

        // Custom Color Palette Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f))
                .border(
                    width = if (EVENT_COLORS.none { it.toArgb() == selectedColor }) 3.dp else 1.dp,
                    color = if (EVENT_COLORS.none { it.toArgb() == selectedColor }) 
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
                .clickable { onOpenCustomPicker() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "Custom Color",
                modifier = Modifier.size(20.dp),
                tint = if (EVENT_COLORS.none { it.toArgb() == selectedColor }) 
                    Color(selectedColor) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Custom RGB Color Picker Dialog
 */
@Composable
fun CustomColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var r by remember { mutableStateOf(initialColor.red) }
    var g by remember { mutableStateOf(initialColor.green) }
    var b by remember { mutableStateOf(initialColor.blue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom Color") },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(r, g, b))
                )
                Spacer(Modifier.height(16.dp))
                ColorSlider(label = "Red", value = r, onValueChange = { r = it }, color = Color.Red)
                ColorSlider(label = "Green", value = g, onValueChange = { g = it }, color = Color.Green)
                ColorSlider(label = "Blue", value = b, onValueChange = { b = it }, color = Color.Blue)
            }
        },
        confirmButton = {
            TextButton(onClick = { onColorSelected(Color(r, g, b)) }) { Text("Select") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ColorSlider(label: String, value: Float, onValueChange: (Float) -> Unit, color: Color) {
    Column {
        Text("$label: ${(value * 255).toInt()}", style = MaterialTheme.typography.labelSmall)
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}

/**
 * Reusable Date Picker Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: LocalDate? = null,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = remember(initialDate) {
        initialDate?.let {
            LocalDateTime(it, LocalTime(0, 0))
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds()
        }
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    
    val selectedDate = remember(datePickerState.selectedDateMillis) {
        derivedStateOf {
            datePickerState.selectedDateMillis?.let {
                Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
            }
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(selectedDate.value)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Reusable Time Picker Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(initialHour = initialTime.hour, initialMinute = initialTime.minute)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onTimeSelected(LocalTime(state.hour, state.minute)) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = state)
        }
    )
}
