package com.pratik.rems.data.repository

import com.pratik.rems.data.local.dao.ReminderDao
import com.pratik.rems.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class handles data operations.
 * It provides a clean API so the rest of the app can easily access data.
 * @Inject tells Hilt to provide the ReminderDao here automatically.
 */
@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    // Get all reminders in a stream (Flow)
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    // Get only pending reminders
    val pendingReminders: Flow<List<Reminder>> = reminderDao.getPendingReminders()

    // Create or update a reminder
    suspend fun upsertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    // Delete a reminder
    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    // Find a reminder by ID
    suspend fun getReminderById(id: Int): Reminder? {
        return reminderDao.getReminderById(id)
    }

    // Search function
    fun searchReminders(query: String): Flow<List<Reminder>> {
        return reminderDao.searchReminders(query)
    }
}