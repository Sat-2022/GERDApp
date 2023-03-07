package com.example.gerdapp.data

import java.util.*

/**********************************************
 * A data structure of event record.
 * parameters:
 *  CaseNumber: String - The case number of the patient with this record
 *  ActivityItem: String - The activity the patients participates in
 *  StartDate: String - The time the patient starts the activity
 *  EndDate: String - The time the patient ends the activity
 *  ActivityNote: String - Some additional notes for this record
 **********************************************/
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
