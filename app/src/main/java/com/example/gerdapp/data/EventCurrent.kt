package com.example.gerdapp.data

import java.util.*

data class EventCurrent(
    val CaseNumber: String,
    val ActivityItem: String,
    val StartDate: String,
    val EndDate: String,
    val ActivityNote: String
) {
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isTimeRecordEmpty()
    }

    fun isSameDate(calendar: Calendar, tag: Int = 0): Boolean {
        var cal = calendar.clone() as Calendar
        val time = when(tag) {
            1 -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                TimeRecord().stringToTimeRecord(EndDate)
            }
            else -> TimeRecord().stringToTimeRecord(StartDate)
        }
        return time.isSameDate(cal)
    }
}
