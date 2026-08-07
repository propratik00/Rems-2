package com.pratik.rems.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class defines the "reminders" table in the database.
 * Each property represents a column in that table.
 */
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val medicineName: String? = null,
    val dosage: String? = null,

    // We store date and time as Long (timestamps) for easy calculations
    val date: Long,
    val time: Long,

    val repeatType: String, // e.g., "Daily", "Weekly", "One Time"
    val repeatInterval: Int = 1,
    val customDays: String? = null, // e.g., "Monday,Wednesday"

    val priority: Int, // 0 for Low, 1 for Medium, 2 for High
    val color: Int, // The color category chosen by the user

    val notificationEnabled: Boolean = true,
    val notificationSound: Boolean = true,
    val notificationVibration: Boolean = true,

    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)