package com.pratik.rems.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratik.rems.alarm.AlarmItem
import com.pratik.rems.alarm.AlarmScheduler
import com.pratik.rems.data.local.entity.Reminder
import com.pratik.rems.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler // Added this
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val reminders: StateFlow<List<Reminder>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) repository.allReminders else repository.searchReminders(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) = _searchQuery.update { query }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            // 1. Cancel the system alarm first
            alarmScheduler.cancel(
                AlarmItem(reminder.id, reminder.time, reminder.title, "")
            )
            // 2. Delete from database
            repository.deleteReminder(reminder)
        }
    }

    fun toggleCompletion(reminder: Reminder) {
        viewModelScope.launch {
            repository.upsertReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }
}