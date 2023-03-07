package com.example.gerdapp.data

import java.util.*

/**********************************************
 * A data structure of drug record.
 * parameters:
 *  CaseNumber: String - The case number of the patient with this record
 *  DrugItem: String - The drug name or drug type taken by the patient
 *  MedicationTime: String - The time the patient take the drug
 *  DrugNote: String - Some additional notes for this record
 **********************************************/
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
