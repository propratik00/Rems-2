package com.pratik.rems.alarm

/**
 * A simple data class to hold alarm details.
 */
data class AlarmItem(
    val id: Int,
    val time: Long,
    val title: String,
    val message: String
)