package com.fakhrinurrohman.myunjapp.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CalendarHeader(
    title: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedContent(
            targetState = title,
            transitionSpec = {
                (fadeIn() + slideInHorizontally { -it / 2 }) togetherWith fadeOut()
            },
            label = "CalendarHeaderAnimation"
        ) { headerText ->
            Text(
                text = headerText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
