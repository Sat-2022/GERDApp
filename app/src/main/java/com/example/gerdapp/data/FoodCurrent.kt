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
     * Or, the record overlaps the given time (tag = 1).
     */
    fun isSameDate(calendar: Calendar, tag: Int = 0): Boolean {
        var cal = calendar.clone() as Calendar
        val time = when(tag) {
            1 -> { // If the end date overlaps the given time, return true
                cal.add(Calendar.DAY_OF_YEAR, 1)
                TimeRecord().stringToTimeRecord(EndDate)
            }
            else -> TimeRecord().stringToTimeRecord(StartDate) // If the start date is at the given time
        }
        return time.isSameDate(cal)
    }
}
