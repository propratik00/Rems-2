package com.pratik.rems.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.pratik.rems.data.local.entity.Reminder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    fun exportRemindersToCsv(context: Context, reminders: List<Reminder>) {
        val csvHeader = "ID,Title,Medicine,Dosage,Date,Time,Status\n"
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        val csvData = StringBuilder().apply {
            append(csvHeader)
            reminders.forEach {
                append("${it.id},")
                append("${it.title},")
                append("${it.medicineName ?: "N/A"},")
                append("${it.dosage ?: "N/A"},")
                append("${dateFormatter.format(Date(it.date))},")
                append("${timeFormatter.format(Date(it.time))},")
                append(if (it.isCompleted) "Done" else "Pending")
                append("\n")
            }
        }

        val file = File(context.cacheDir, "my_reminders.csv")
        file.writeText(csvData.toString())

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export CSV"))
    }
}