package com.example.gerdapp.data

import java.util.*

data class DrugCurrent(
    val CaseNumber: String,
    val DrugItem: String,
    val MedicationTime: String,
    val DrugNote: String
) {
    /*
     * Check if the record is empty.
     */
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(MedicationTime)
        return time.isTimeRecordEmpty()
    }

    /*
     * Check if the record is at the same date as a given time.
     */
    fun isEqual(calendar: Calendar): Boolean {
        val time = TimeRecord().stringToTimeRecord(MedicationTime)
        return time.isEqual(calendar)
    }
}
