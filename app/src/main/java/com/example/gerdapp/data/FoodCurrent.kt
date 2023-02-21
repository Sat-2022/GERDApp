package com.example.gerdapp.data

import java.util.*

data class FoodCurrent(
    val CaseNumber: String,
    val FoodItem: String,
    val StartDate: String,
    val EndDate: String,
    val FoodNote: String
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
    fun isEqual(calendar: Calendar): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate) // If the start date is at the given time
        return time.isEqual(calendar)
    }

    fun isBefore(calendar: Calendar): Boolean {
        val time = TimeRecord().stringToTimeRecord(EndDate)
        return time.isEqual(calendar)
    }

    fun isAfter(calendar: Calendar): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isEqual(calendar)
    }

    fun isSameDate(): Boolean {
        val startTime = TimeRecord().stringToTimeRecord(StartDate)
        val endTime = TimeRecord().stringToTimeRecord(EndDate)
        return startTime.isEqual(endTime)
    }
}
