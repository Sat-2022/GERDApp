package com.example.gerdapp.data

data class FoodCurrent(
    val CaseNumber: String,
    val FoodItem: String,
    val StartDate: String,
    val EndDate: String,
    val FoodNote: String
) {
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isTimeRecordEmpty()
    }
}
