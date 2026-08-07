package com.pratik.rems.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pratik.rems.receiver.ReminderReceiver
import java.time.ZoneId

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem) {
        // This Intent tells Android which class to open when the alarm goes off
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", item.title)
            putExtra("EXTRA_MESSAGE", item.message)
            putExtra("EXTRA_ID", item.id)
        }

        // We use setExactAndAllowWhileIdle so the alarm works even if the phone is in battery-saving mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time,
            PendingIntent.getBroadcast(
                context,
                item.id, // Unique ID for this alarm
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id,
                Intent(context, ReminderReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}