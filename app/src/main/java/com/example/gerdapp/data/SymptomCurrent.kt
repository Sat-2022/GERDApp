package com.example.gerdapp.data

import java.util.*

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
     * Or, the record overlaps the given time (tag = 1).
     */
    fun isSameDate(calendar: Calendar, tag: Int = 0): Boolean {
        var cal = calendar.clone() as Calendar
        val time = when(tag) {
            1 -> { // If the end date overlaps the given time, return true
                cal.add(Calendar.DAY_OF_YEAR, 1)
                TimeRecord().stringToTimeRecord(EndDate)
            }
            else -> TimeRecord().stringToTimeRecord(StartDate) // If the start date is at the given time
        }
        return time.isSameDate(cal)
    }


    /*
     * Return the record in string.
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
            symptomItemString = "$SymptomOther"
        }

        return symptomItemString
    }
}
