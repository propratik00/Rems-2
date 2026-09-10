package com.pratik.rems.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class TutorialStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun FirstLaunchTutorialDialog(
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        TutorialStep(
            title = "Welcome to Remi!",
            subtitle = "Your Smart Medicine & Task Companion",
            description = "Track your daily medicine prescriptions, dosages, and critical tasks so you never miss a schedule.",
            icon = Icons.Default.MedicalServices,
            accentColor = Color(0xFF4F46E5) // Indigo
        ),
        TutorialStep(
            title = "Flexible Smart Alarms",
            subtitle = "Daily, Weekly or Custom Repeats",
            description = "Set exact alarm times with repeating cycles. Remi ensures notifications fire even when your device is in battery-saver mode.",
            icon = Icons.Default.Alarm,
            accentColor = Color(0xFF0D9488) // Teal
        ),
        TutorialStep(
            title = "Color-Coded Priorities",
            subtitle = "High, Medium & Low Urgency",
            description = "Tag urgent prescriptions as High Priority. Check off items with one tap when completed or snooze them for 10 minutes.",
            icon = Icons.Default.CheckCircle,
            accentColor = Color(0xFFE11D48) // Rose
        ),
        TutorialStep(
            title = "Export & Multi-Language",
            subtitle = "CSV Sharing for Doctor Visits",
            description = "Export your reminder logs to a clean CSV file to share with your doctor, and switch between English, Hindi, and Marathi.",
            icon = Icons.Default.Share,
            accentColor = Color(0xFFD97706) // Amber
        )
    )

    val current = steps[currentStep]

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Indicator Badge
                Surface(
                    shape = CircleShape,
                    color = current.accentColor.copy(alpha = 0.12f),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Step ${currentStep + 1} of ${steps.size}",
                        color = current.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Animated Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(current.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = current.icon,
                        contentDescription = null,
                        tint = current.accentColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Title and Subtitle
                Text(
                    text = current.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = current.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Description
                Text(
                    text = current.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == currentStep) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentStep) current.accentColor
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons (Previous / Next / Finish)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        TextButton(onClick = { currentStep-- }) {
                            Text("Back")
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text("Skip")
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < steps.size - 1) {
                                currentStep++
                            } else {
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = current.accentColor)
                    ) {
                        Text(
                            text = if (currentStep == steps.size - 1) "Get Started" else "Next",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}