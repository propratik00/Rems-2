package com.pratik.rems.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pratik.rems.R
import com.pratik.rems.ui.viewmodel.AddReminderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    onBack: () -> Unit,
    reminderId: Int = -1,
    viewModel: AddReminderViewModel = hiltViewModel()
) {
    // Logic: Load data if we are in Edit Mode
    LaunchedEffect(reminderId) {
        if (reminderId != -1) {
            viewModel.loadReminder(reminderId)
        }
    }

    // Logic: Close screen when save is successful
    LaunchedEffect(viewModel.isSaved) {
        if (viewModel.isSaved) {
            onBack()
        }
    }

    // Picker States
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.selectedDate
    )
    val timePickerState = rememberTimePickerState(
        initialHour = viewModel.selectedHour,
        initialMinute = viewModel.selectedMinute
    )

    // Visibility States
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedRepeat by remember { mutableStateOf(false) }

    // Repeat options list (Logic stays English, Display is translated in the dropdown)
    val repeatOptions = listOf(
        "One Time", "Daily", "Weekly", "Monthly",
        "Weekdays Only", "Weekends Only"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (reminderId == -1) stringResource(R.string.new_reminder) else stringResource(R.string.edit_reminder),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // TITLE SECTION
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text(stringResource(R.string.reminder_title)) },
                placeholder = { Text(stringResource(R.string.title_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // MEDICINE DETAILS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.medicineName,
                    onValueChange = { viewModel.medicineName = it },
                    label = { Text(stringResource(R.string.medicine_name)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = viewModel.dosage,
                    onValueChange = { viewModel.dosage = it },
                    label = { Text(stringResource(R.string.dosage)) },
                    placeholder = { Text(stringResource(R.string.dosage_placeholder)) },
                    modifier = Modifier.weight(0.6f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            // DESCRIPTION
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = MaterialTheme.shapes.medium
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            // DATE SELECTION
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(stringResource(R.string.date), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(formatDate(viewModel.selectedDate), style = MaterialTheme.typography.bodyLarge)
                    }
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }

            // TIME SELECTION
            OutlinedCard(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(stringResource(R.string.time), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(
                            String.format("%02d:%02d %s",
                                if (viewModel.selectedHour % 12 == 0) 12 else viewModel.selectedHour % 12,
                                viewModel.selectedMinute,
                                if (viewModel.selectedHour < 12) "AM" else "PM"
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.Schedule, contentDescription = null)
                }
            }

            // REPEAT DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedRepeat,
                onExpandedChange = { expandedRepeat = !expandedRepeat }
            ) {
                OutlinedTextField(
                    value = viewModel.repeatType, // Logic remains English for DB
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.repeat_freq)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRepeat) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(
                    expanded = expandedRepeat,
                    onDismissRequest = { expandedRepeat = false }
                ) {
                    repeatOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.repeatType = option
                                expandedRepeat = false
                            }
                        )
                    }
                }
            }

            // PRIORITY SECTION
            Text(stringResource(R.string.priority_level), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val priorities = listOf(
                    stringResource(R.string.low),
                    stringResource(R.string.medium),
                    stringResource(R.string.high)
                )
                priorities.forEachIndexed { index, label ->
                    FilterChip(
                        selected = viewModel.priority == index,
                        onClick = { viewModel.priority = index },
                        label = { Text(label) },
                        leadingIcon = if (viewModel.priority == index) {
                            { Box(Modifier.size(8.dp).padding(2.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SAVE BUTTON
            Button(
                onClick = { viewModel.saveReminder() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = viewModel.title.isNotBlank()
            ) {
                Text(
                    if (reminderId == -1) stringResource(R.string.schedule_reminder) else stringResource(R.string.update_reminder),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- DIALOGS ---

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.selectedDate = datePickerState.selectedDateMillis ?: viewModel.selectedDate
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.selectedHour = timePickerState.hour
                    viewModel.selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
            },
            title = { Text(stringResource(R.string.time)) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}