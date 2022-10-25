package com.example.gerdapp.data

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
}
