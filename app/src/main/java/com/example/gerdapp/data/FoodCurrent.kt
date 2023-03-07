package com.example.gerdapp.data

import java.util.*

/**********************************************
 * A data structure of food record.
 * parameters:
 *  CaseNumber: String - The case number of the patient with this record
 *  FoodItem: String - The food the patients taken
 *  StartDate: String - The time the patient starts eating
 *  EndDate: String - The time the patient ends eating
 *  FoodNote: String - Some additional notes for this record
 **********************************************/
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

    /*
    * Check if the record ends before the given date
    */
    fun isBefore(calendar: Calendar): Boolean {
        val time = TimeRecord().stringToTimeRecord(EndDate)
        return time.isEqual(calendar)
    }

    /*
     * Check if the record starts after the given date
     */
    fun isAfter(calendar: Calendar): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isEqual(calendar)
    }

    /*
     * Check if the record has the same start date and end date
     */
    fun isSameDate(): Boolean {
        val startTime = TimeRecord().stringToTimeRecord(StartDate)
        val endTime = TimeRecord().stringToTimeRecord(EndDate)
        return startTime.isEqual(endTime)
    }
}
