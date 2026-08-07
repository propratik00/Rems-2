package com.pratik.rems.data.local.dao

import androidx.room.*
import com.pratik.rems.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // Adds a new reminder. If it already exists, replace it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    // Updates an existing reminder (e.g., when marking as done)
    @Update
    suspend fun updateReminder(reminder: Reminder)

    // Deletes a specific reminder
    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    // Get a single reminder by its ID
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Int): Reminder?

    // Get all reminders sorted by date and time (Newest first)
    @Query("SELECT * FROM reminders ORDER BY date ASC, time ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    // Get only reminders that are not completed yet
    @Query("SELECT * FROM reminders WHERE isCompleted = 0")
    fun getPendingReminders(): Flow<List<Reminder>>

    // Search for reminders by Title or Medicine Name
    @Query("SELECT * FROM reminders WHERE title LIKE '%' || :query || '%' OR medicineName LIKE '%' || :query || '%'")
    fun searchReminders(query: String): Flow<List<Reminder>>
}