package com.pratik.rems.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pratik.rems.alarm.AlarmItem
import com.pratik.rems.alarm.AlarmScheduler
import com.pratik.rems.data.local.dao.ReminderDao
import com.pratik.rems.data.local.entity.Reminder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject lateinit var reminderDao: ReminderDao
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val reminderId = intent?.getIntExtra("EXTRA_ID", -1) ?: -1
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (reminderId == -1) return

        when (action) {
            "ACTION_DONE" -> {
                // Mark as done in Database
                CoroutineScope(Dispatchers.IO).launch {
                    val reminder = reminderDao.getReminderById(reminderId)
                    reminder?.let {
                        reminderDao.updateReminder(it.copy(isCompleted = true))
                    }
                    notificationManager.cancel(reminderId)
                }
            }
            "ACTION_SNOOZE" -> {
                // Schedule a new alarm 10 minutes from now
                val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000)
                val title = intent.getStringExtra("EXTRA_TITLE") ?: "Reminder"

                alarmScheduler.schedule(
                    AlarmItem(reminderId, snoozeTime, title, "Snoozed Reminder")
                )
                notificationManager.cancel(reminderId)
            }
            "ACTION_DISMISS" -> {
                notificationManager.cancel(reminderId)
            }
        }
    }
}