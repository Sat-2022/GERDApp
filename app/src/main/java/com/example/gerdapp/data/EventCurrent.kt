package com.example.gerdapp.data

import java.util.*

data class EventCurrent(
    val CaseNumber: String,
    val ActivityItem: String,
    val StartDate: String,
    val EndDate: String,
    val ActivityNote: String
) {
    /*
     * Check if the record is empty.
     */
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isTimeRecordEmpty()
    }

    /*
     * Check if the record is at the same date as a given time.
     */
    fun isSameDate(calendar: Calendar): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate) // If the start date is at the given time
        return time.isEqual(calendar)
    }
}
