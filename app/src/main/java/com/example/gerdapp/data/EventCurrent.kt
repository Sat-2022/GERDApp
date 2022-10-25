package com.example.gerdapp.data

data class EventCurrent(
    val CaseNumber: String,
    val ActivityItem: String,
    val StartDate: String,
    val EndDate: String,
    val ActivityNote: String
) {
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isTimeRecordEmpty()
    }
}
