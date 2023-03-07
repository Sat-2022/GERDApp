package com.example.gerdapp.data

import java.util.*

/**********************************************
 * A data structure of event record.
 * parameters:
 *  CaseNumber: String - The case number of the patient with this record
 *  SymptomItem: String - Specific symptoms the patient has
 *  SymptomOther: String - Symptoms other than the specified ones, added by the patients
 *  StartDate: String - The time the symptom starts
 *  EndDate: String - The time the symptom ends
 *  SymptomNote: String - Some additional notes for this record
 **********************************************/
data class SymptomCurrent(
    val CaseNumber: String,
    val SymptomItem: String,
    val SymptomOther: String,
    val StartDate: String,
    val EndDate: String,
    val SymptomNote: String
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

    /*
     * Format the record to string.
     */
    fun symptomToString(): String {
        var symptomItemString = ""
        if(SymptomItem != "") { // Show default symptoms
            val symptomItem = SymptomItem.split(",").toTypedArray()
            var itemCount = 0
            for (i in symptomItem) {

                if (itemCount > 2) { // If there is more than 2 symptoms recorded, show with "..."
                    symptomItemString += ", ..."
                    break
                }

                if (i != symptomItem[0]) symptomItemString += ", "

                when (i) {
                    "1" -> {
                        symptomItemString += "咳嗽"
                        itemCount++
                    }
                    "2" -> {
                        symptomItemString += "火燒心"
                        itemCount++
                    }
                    "3" -> {
                        symptomItemString += "胃酸逆流"
                        itemCount++
                    }
                    "4" -> {
                        symptomItemString += "胸痛"
                        itemCount++
                    }
                    "5" -> {
                        symptomItemString += "口有酸味"
                        itemCount++
                    }
                    "6" -> {
                        symptomItemString += "聲音沙啞"
                        itemCount++
                    }
                    "7" -> {
                        symptomItemString += "吞嚥困難"
                        itemCount++
                    }
                    "8" -> {
                        symptomItemString += "嘔氣"
                        itemCount++
                    }
                }
            }
        } else { // If there is no default symptom recorded, show symptoms recorded in others
            symptomItemString = SymptomOther
        }

        return symptomItemString
    }
}
