package com.pratik.rems.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pratik.rems.data.local.entity.Reminder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderItem(
    reminder: Reminder,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    // Determine priority color
    val priorityColor = when (reminder.priority) {
        2 -> Color(0xFFE57373) // High - Red
        1 -> Color(0xFFFFB74D) // Medium - Orange
        else -> Color(0xFF81C784) // Low - Green
    }

    // Format display time
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val displayTime = timeFormatter.format(Date(reminder.time))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onItemClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Completion Checkbox
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 2. Reminder Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Show Medicine Name and Dosage if available
                if (!reminder.medicineName.isNullOrBlank()) {
                    Text(
                        text = "💊 ${reminder.medicineName} (${reminder.dosage ?: "No dose"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Time and Repeat Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$displayTime • ${reminder.repeatType}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            // 3. Priority Indicator and Delete Action
            Column(horizontalAlignment = Alignment.End) {
                // Small priority dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(priorityColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Reminder",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}