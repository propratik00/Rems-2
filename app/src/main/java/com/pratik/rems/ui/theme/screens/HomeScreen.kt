package com.pratik.rems.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pratik.rems.R
import com.pratik.rems.ui.components.ReminderItem
import com.pratik.rems.ui.viewmodel.ReminderViewModel
import com.pratik.rems.utils.CsvExporter
import com.pratik.rems.utils.LanguageManager
import android.content.Context
import com.pratik.rems.ui.components.FirstLaunchTutorialDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsState()
    val sharedPreferences = remember {
        context.getSharedPreferences("remi_user_prefs", Context.MODE_PRIVATE)
    }


    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showTutorialDialog by remember {
        mutableStateOf(!sharedPreferences.getBoolean("has_seen_tutorial", false))
    }


    // --- LANGUAGE DIALOG STATE ---
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showTutorialDialog) {
        FirstLaunchTutorialDialog(
            onDismiss = {
                // Mark tutorial as completed so it won't show again!
                sharedPreferences.edit()
                    .putBoolean("has_seen_tutorial", true)
                    .apply()
                showTutorialDialog = false
            }
        )
    }


    if (showLanguageDialog) {
        LanguageDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { langCode ->
                LanguageManager.setLanguage(langCode)
                showLanguageDialog = false
            }
        )
    }

    // Filtering logic
    val filteredReminders = reminders.filter {
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) ||
                (it.medicineName?.contains(searchQuery, ignoreCase = true) ?: false)
        val matchesFilter = when (selectedFilter) {
            "Pending" -> !it.isCompleted
            "Completed" -> it.isCompleted
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.my_reminders), fontWeight = FontWeight.Bold)
                },
                actions = {
                    // 1. LANGUAGE SELECTOR BUTTON
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Icon(Icons.Default.Language, contentDescription = "Change Language")
                    }

                    // 2. EXPORT CSV BUTTON
                    IconButton(onClick = {
                        CsvExporter.exportRemindersToCsv(context, reminders)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.export_csv))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_new)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            // 2. STATISTICS SUMMARY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.pending),
                    count = reminders.count { !it.isCompleted }.toString(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.completed),
                    count = reminders.count { it.isCompleted }.toString(),
                    color = Color(0xFFC8E6C9), // Soft Green
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. FILTER CHIPS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // List of filters (Note: Logic keys remain English, display labels are translated)
                val filterOptions = listOf("All", "Pending", "Completed")
                filterOptions.forEach { filterKey ->
                    val displayLabel = when(filterKey) {
                        "Pending" -> stringResource(R.string.pending)
                        "Completed" -> stringResource(R.string.completed)
                        else -> filterKey // "All" stays "All" or add it to strings.xml if needed
                    }

                    FilterChip(
                        selected = selectedFilter == filterKey,
                        onClick = { selectedFilter = filterKey },
                        label = { Text(displayLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. THE LIST
            if (filteredReminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isEmpty()) stringResource(R.string.no_reminders) else stringResource(R.string.no_results),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        if (searchQuery.isEmpty()) {
                            TextButton(onClick = onAddClick) {
                                Text(stringResource(R.string.create_first))
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = filteredReminders,
                        key = { it.id }
                    ) { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onCheckedChange = { viewModel.toggleCompletion(reminder) },
                            onDeleteClick = { viewModel.deleteReminder(reminder) },
                            onItemClick = { onEditClick(reminder.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = count, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LanguageDialog(onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onLanguageSelected("en") }, modifier = Modifier.fillMaxWidth()) {
                    Text("English", fontSize = 18.sp)
                }
                TextButton(onClick = { onLanguageSelected("hi") }, modifier = Modifier.fillMaxWidth()) {
                    Text("हिन्दी (Hindi)", fontSize = 18.sp)
                }
                TextButton(onClick = { onLanguageSelected("mr") }, modifier = Modifier.fillMaxWidth()) {
                    Text("मराठी (Marathi)", fontSize = 18.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}