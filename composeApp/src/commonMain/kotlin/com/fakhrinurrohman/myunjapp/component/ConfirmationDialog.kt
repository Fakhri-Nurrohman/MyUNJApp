package com.fakhrinurrohman.myunjapp.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(dismissButtonText)
            }
        }
    )
}
