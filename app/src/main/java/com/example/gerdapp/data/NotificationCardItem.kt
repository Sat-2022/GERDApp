package com.example.gerdapp.data

/**********************************************
 * Data structure of notification card items
 * parameters:
 *  CaseNumber: String - the case number of the patient who receive this notification
 *  ReturnItem: String - the title of the notification
 *  ReturnDate: String - the date of the notification
 *  ReturnDesc: String - the descriptions about the notification
 **********************************************/
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
    /*
     * Check if the notification card is empty.
     * Calls isTimeRecordEmpty to check if return date is empty
     */
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(ReturnDate)
        return time.isTimeRecordEmpty()
    }
}
