package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.ConfirmationDialog
import com.fakhrinurrohman.myunjapp.data.EventType
import com.fakhrinurrohman.myunjapp.data.UserEvent
import com.fakhrinurrohman.myunjapp.util.toReadableDate

@Composable
fun UserEventDetailScreen(
    event: UserEvent,
    onDeleteClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onToggleCompletion: (String, Boolean) -> Unit,
    onClose: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    // Resolve the data-driven colors
    val eventColor = event.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
    val eventContainerColor = event.color?.let { Color(it).copy(alpha = 0.15f) } ?: MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (event.type) {
                EventType.HOMEWORK -> Icons.AutoMirrored.Filled.LibraryBooks
                EventType.EXAM -> Icons.Default.Event
                else -> Icons.Default.CalendarToday
            }
            
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(eventContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = eventColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = event.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelLarge,
                    color = eventColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Details
        DetailRow(icon = Icons.Default.CalendarToday, label = "Date", value = event.date.toReadableDate())
        
        if (event.startTime != null) {
            val timeText = if (event.endTime != null) "${event.startTime} - ${event.endTime}" else "${event.startTime}"
            DetailRow(icon = Icons.Default.Schedule, label = "Time", value = timeText)
        }

        if (event.description.isNotBlank()) {
            DetailRow(
                icon = Icons.AutoMirrored.Filled.Notes,
                label = "Description", 
                value = event.description
            )
        }

        if (event.type == EventType.HOMEWORK) {
            DetailRow(
                icon = if (event.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, 
                label = "Status", 
                value = if (event.isCompleted) "Completed" else "Pending",
                activeColor = if (event.isCompleted) Color(0xFF4CAF50) else eventColor, // Green for completed, data color for pending
                onClick = { onToggleCompletion(event.id, !event.isCompleted) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Delete")
            }

            Button(
                onClick = { onEditClick(event.id) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = eventColor)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
    }

    if (showDeleteConfirmation) {
        ConfirmationDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            onConfirm = {
                onDeleteClick(event.id)
                showDeleteConfirmation = false
            },
            title = "Delete ${event.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
            text = "Are you sure you want to delete this? This action cannot be undone.",
            confirmButtonText = "Delete"
        )
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector, 
    label: String, 
    value: String,
    activeColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = activeColor ?: MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value, 
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (onClick != null) FontWeight.Bold else FontWeight.Normal,
                color = activeColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
