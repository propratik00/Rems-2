package com.pratik.rems.utils

import java.util.*

object ReminderUtils {

    fun calculateNextOccurrence(
        currentTime: Long,
        repeatType: String,
        repeatInterval: Int = 1,
        customDays: String? = null
    ): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = currentTime }

        when (repeatType) {
            "Daily" -> calendar.add(Calendar.DAY_OF_YEAR, repeatInterval)
            "Weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, repeatInterval)
            "Monthly" -> calendar.add(Calendar.MONTH, repeatInterval)
            "Yearly" -> calendar.add(Calendar.YEAR, repeatInterval)
            "Weekdays Only" -> {
                do {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                } while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            }
            "Weekends Only" -> {
                do {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                } while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            }
            "Custom Days" -> {
                val selectedDays = customDays?.split(",")?.map { it.trim() } ?: emptyList()
                if (selectedDays.isNotEmpty()) {
                    val dayMap = mapOf(
                        "Sunday" to Calendar.SUNDAY, "Monday" to Calendar.MONDAY,
                        "Tuesday" to Calendar.TUESDAY, "Wednesday" to Calendar.WEDNESDAY,
                        "Thursday" to Calendar.THURSDAY, "Friday" to Calendar.FRIDAY,
                        "Saturday" to Calendar.SATURDAY
                    )
                    val targetDays = selectedDays.mapNotNull { dayMap[it] }

                    do {
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                    } while (!targetDays.contains(calendar.get(Calendar.DAY_OF_WEEK)))
                }
            }
        }
        return calendar.timeInMillis
    }
}