package com.pratik.rems.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pratik.rems.data.local.dao.ReminderDao
import com.pratik.rems.data.local.entity.Reminder

/**
 * This class serves as the main connection point to your app's persisted data.
 * We list all entities (tables) here. Currently, we only have one: Reminder.
 */
@Database(
    entities = [Reminder::class],
    version = 2,
    exportSchema = false
)
abstract class RemiDatabase : RoomDatabase() {

    // This allows the rest of the app to access the DAO functions
    abstract val reminderDao: ReminderDao
}