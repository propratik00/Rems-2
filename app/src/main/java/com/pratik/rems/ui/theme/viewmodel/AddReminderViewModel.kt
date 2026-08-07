package com.pratik.rems.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratik.rems.alarm.AlarmItem
import com.pratik.rems.alarm.AlarmScheduler
import com.pratik.rems.data.local.entity.Reminder
import com.pratik.rems.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    // Internal ID to track if we are editing an existing reminder
    private var currentReminderId: Int = 0

    // --- Form State ---
    var title by mutableStateOf("")
    var medicineName by mutableStateOf("")
    var dosage by mutableStateOf("")
    var description by mutableStateOf("")

    // Separate Date and Time state
    var selectedDate by mutableLongStateOf(System.currentTimeMillis())
    var selectedHour by mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
    var selectedMinute by mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE))

    var repeatType by mutableStateOf("One Time")
    var priority by mutableIntStateOf(1) // 0: Low, 1: Medium, 2: High
    var color by mutableIntStateOf(0xFF6200EE.toInt()) // Default Material Purple

    // Status flag for UI navigation
    var isSaved by mutableStateOf(false)
        private set

    /**
     * Loads an existing reminder into the form state if we are in "Edit Mode".
     */
    fun loadReminder(id: Int) {
        if (id <= 0 || currentReminderId == id) return

        viewModelScope.launch {
            repository.getReminderById(id)?.let { reminder ->
                currentReminderId = reminder.id
                title = reminder.title
                medicineName = reminder.medicineName ?: ""
                dosage = reminder.dosage ?: ""
                description = reminder.description
                selectedDate = reminder.date
                repeatType = reminder.repeatType
                priority = reminder.priority
                color = reminder.color

                // Extract hour and minute from the saved timestamp
                val cal = Calendar.getInstance().apply { timeInMillis = reminder.time }
                selectedHour = cal.get(Calendar.HOUR_OF_DAY)
                selectedMinute = cal.get(Calendar.MINUTE)
            }
        }
    }

    /**
     * Merges form data, saves to Database, and schedules the System Alarm.
     */
    fun saveReminder() {
        if (title.isBlank()) return

        viewModelScope.launch {
            // 1. Calculate the exact Trigger Time (Merge Date + Time)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selectedDate
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If the time set is in the past for today, move it to tomorrow (for Daily etc)
            // But for One Time, we keep it as is (logic handles it)
            var triggerTime = calendar.timeInMillis
            if (triggerTime <= System.currentTimeMillis() && repeatType != "One Time") {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                triggerTime = calendar.timeInMillis
            }

            // 2. Prepare Entity Object
            val reminder = Reminder(
                id = currentReminderId, // If 0, Room will auto-generate
                title = title,
                description = description,
                medicineName = medicineName.ifBlank { null },
                dosage = dosage.ifBlank { null },
                date = selectedDate,
                time = triggerTime,
                repeatType = repeatType,
                priority = priority,
                color = color,
                isCompleted = false,
                updatedAt = System.currentTimeMillis()
            )

            // 3. Persist to Database
            val generatedId = repository.upsertReminder(reminder)
            val finalId = if (currentReminderId == 0) generatedId.toInt() else currentReminderId

            // 4. Schedule the System Alarm
            // We use the medicine name in the notification message if it's a medicine reminder
            val messageText = if (!medicineName.isNullOrBlank()) {
                "Time for $medicineName ($dosage)"
            } else {
                description.ifBlank { "You have a task scheduled." }
            }

            alarmScheduler.schedule(
                AlarmItem(
                    id = finalId,
                    time = triggerTime,
                    title = title,
                    message = messageText
                )
            )

            // 5. Trigger navigation back to Home
            isSaved = true
        }
    }
}