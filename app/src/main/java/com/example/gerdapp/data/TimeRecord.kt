package com.example.gerdapp.data

import java.util.*

class TimeRecord {
    var YEAR = 1911
    var MONTH = 1
    var DAY = 1
    var HOUR = 0
    var MIN = 0
    var SEC = 0
    var DAY_OF_WEEK = 0

    fun setTimeRecord(year: Int, month: Int, day: Int, hour: Int, min: Int, sec: Int) {
        YEAR = year
        MONTH = month
        DAY = day
        HOUR = hour
        MIN = min
        SEC = sec
    }

    fun isTimeRecordEmpty(): Boolean {
        if(YEAR == 1911 && MONTH == 1 && DAY == 1 && HOUR == 0 && MIN == 0 && SEC == 0) return true
        return false
    }

    fun stringToTimeRecord(string: String): TimeRecord {
        var timeRecord = TimeRecord()

        // 0123456789012345678901
        // yyyy-mm-ddTHH:mm:ss.ss

        timeRecord.YEAR = Integer.parseInt(string[0].toString())*1000 + Integer.parseInt(string[1].toString())*100 + Integer.parseInt(string[2].toString())*10 + Integer.parseInt(string[3].toString())*1
        timeRecord.MONTH = Integer.parseInt(string[5].toString())*10 + Integer.parseInt(string[6].toString())*1
        timeRecord.DAY = Integer.parseInt(string[8].toString())*10 + Integer.parseInt(string[9].toString())*1
        timeRecord.HOUR = Integer.parseInt(string[11].toString())*10 + Integer.parseInt(string[12].toString())*1
        timeRecord.MIN = Integer.parseInt(string[14].toString())*10 + Integer.parseInt(string[15].toString())*1
        timeRecord.SEC = Integer.parseInt(string[17].toString())*10 + Integer.parseInt(string[18].toString())*1

        return timeRecord
    }

    private fun isToday(): Boolean {
        val calendar = Calendar.getInstance()

        if(this.YEAR != calendar[Calendar.YEAR]) return false
        if(this.MONTH != calendar[Calendar.MONTH]+1) return false
        if(this.DAY != calendar[Calendar.DAY_OF_MONTH]) return false

        return true
    }

    fun isEqual(calendar: Calendar): Boolean {
        if(this.YEAR != calendar[Calendar.YEAR]) return false
        if(this.MONTH != calendar[Calendar.MONTH]+1) return false
        if(this.DAY != calendar[Calendar.DAY_OF_MONTH]) return false

        return true
    }

    fun isEqual(timeRecord: TimeRecord): Boolean {
        if(this.YEAR != timeRecord.YEAR) return false
        if(this.MONTH != timeRecord.MONTH) return false
        if(this.DAY != timeRecord.DAY) return false

        return true
    }

    override fun toString(): String {
        var string = ""

        if(this.isToday()) {
            string += this.HOUR.toString() + " 時 "
            string += this.MIN.toString() + "分"
        } else {
//            string += this.YEAR.toString() + " 年 "
            string += this.MONTH.toString() + " 月 "
            string += this.DAY.toString() + " 日 "
//            string += this.HOUR.toString() + " 時 "
//            string += this.MIN.toString() + "分"
        }

        return string
    }

    fun toString(flag: Int = 0): String {
        if(flag == 0){
            return this.toString()
        }

        var string = ""

        if(flag == 1) {
            string += this.MONTH.toString() + " 月 "
            string += this.DAY.toString() + " 日 "
        } else if(flag == 2) {
            string += this.HOUR.toString() + " 時 "
            string += this.MIN.toString() + "分"
        }
        return string
    }

    fun timeRecordToFloat(): Float {
        return (this.HOUR*10000 + this.MIN*10000/60 + this.SEC*100/60).toFloat()
    }

}