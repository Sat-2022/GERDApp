package com.example.gerdapp.data

data class SymptomCurrent(
    val CaseNumber: String,
    val SymptomItem: String,
    val SymptomOther: String,
    val StartDate: String,
    val EndDate: String,
    val SymptomNote: String
) {
    fun isEmpty(): Boolean {
        val time = TimeRecord().stringToTimeRecord(StartDate)
        return time.isTimeRecordEmpty()
    }

    fun symptomToString(): String {
        var symptomItemString = ""
        if(SymptomItem != "") {
            val symptomItem = SymptomItem.split(",").toTypedArray()
            var itemCount = 0
            for (i in symptomItem) {

                if (itemCount > 2) {
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
        } else {
            symptomItemString = "$SymptomOther"
        }

        return symptomItemString
    }
}
