package com.pratik.rems.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pratik.rems.alarm.AlarmItem
import com.pratik.rems.alarm.AlarmScheduler
import com.pratik.rems.data.local.dao.ReminderDao
import com.pratik.rems.notification.NotificationHelper
import com.pratik.rems.utils.ReminderUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var reminderDao: ReminderDao
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getIntExtra("EXTRA_ID", -1) ?: -1
        if (id == -1) return

        context?.let { ctx ->
            val notificationHelper = NotificationHelper(ctx)

            CoroutineScope(Dispatchers.IO).launch {
                val reminder = reminderDao.getReminderById(id)
                reminder?.let {
                    // 1. Show the notification
                    notificationHelper.showNotification(
                        it.id, it.title,
                        if (!it.medicineName.isNullOrBlank()) "${it.medicineName} - ${it.dosage}" else it.description
                    )

                    // 2. If it's a repeating reminder, schedule the next one
                    if (it.repeatType != "One Time") {
                        val nextTime = ReminderUtils.calculateNextOccurrence(
                            it.time, it.repeatType, it.repeatInterval, it.customDays
                        )

                        // Update database with the new time
                        val nextReminder = it.copy(time = nextTime)
                        reminderDao.updateReminder(nextReminder)

                        // Schedule the next alarm
                        alarmScheduler.schedule(
                            AlarmItem(nextReminder.id, nextTime, nextReminder.title, nextReminder.description)
                        )
                    }
                }
            }
        }
    }
}