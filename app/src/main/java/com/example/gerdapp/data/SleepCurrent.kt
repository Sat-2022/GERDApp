package com.example.gerdapp.data

data class SleepCurrent(
    val CaseNumber: String,
    val StartDate: String,
    val EndDate: String,
    val SleepNote: String
) {
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isTimeRecordEmpty()
    }
}
