package com.pratik.rems.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pratik.rems.MainActivity
import com.pratik.rems.R
import com.pratik.rems.receiver.NotificationActionReceiver

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(id: Int, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "medicine_channel", "Reminders", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Action: Done
        val doneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_DONE"
            putExtra("EXTRA_ID", id)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context, id + 1, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("EXTRA_ID", id)
            putExtra("EXTRA_TITLE", title)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, id + 2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "medicine_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(0, "✓ Mark as Done", donePendingIntent)
            .addAction(0, "⏰ Snooze 10m", snoozePendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}