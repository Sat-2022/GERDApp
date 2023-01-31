package com.example.gerdapp.data

import java.util.*

data class DrugCurrent(
    val CaseNumber: String,
    val DrugItem: String,
    val MedicationTime: String,
    val DrugNote: String
) {
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(MedicationTime)
        return time.isTimeRecordEmpty()
    }

    fun isSameDate(calendar: Calendar, tag: Int = 0): Boolean {
        var cal = calendar.clone() as Calendar
        val time = TimeRecord().stringToTimeRecord(MedicationTime)
        return time.isSameDate(cal)
    }
}
