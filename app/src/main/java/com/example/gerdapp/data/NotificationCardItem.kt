package com.example.gerdapp.data

data class NotificationCardItem(
//    val Rmno: Int,
    val CaseNumber: String,
//    val Uid: String,
    val ReturnItem: String,
    val ReturnDate: String,
    val ReturnDesc: String,
//    val CreateId: String,
//    val CreateDateTime: String,
//    val LastId: String,
//    val LastDateTime: String
) {
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(ReturnDate)
        return time.isTimeRecordEmpty()
    }
}
