package com.pratik.rems.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pratik.rems.alarm.AlarmItem
import com.pratik.rems.alarm.AlarmScheduler
import com.pratik.rems.data.local.dao.ReminderDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var reminderDao: ReminderDao
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                // Get all reminders that are NOT completed and are in the future
                val reminders = reminderDao.getPendingReminders().first()
                reminders.forEach { reminder ->
                    if (reminder.time > System.currentTimeMillis()) {
                        alarmScheduler.schedule(
                            AlarmItem(
                                id = reminder.id,
                                time = reminder.time,
                                title = reminder.title,
                                message = reminder.medicineName ?: reminder.description
                            )
                        )
                    }
                }
            }
        }
    }
}