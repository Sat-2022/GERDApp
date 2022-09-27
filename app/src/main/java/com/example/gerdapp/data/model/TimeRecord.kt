package com.example.gerdapp.data.model

import android.util.Log
import com.example.gerdapp.R

class TimeRecord {
    var YEAR = 0
    var MONTH = 0
    var DAY = 0
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
        if(YEAR == 0 && MONTH == 0 && DAY == 0 && HOUR == 0 && MIN == 0 && SEC == 0) return true
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

    override fun toString(): String {
        var string = ""

//        string += this.YEAR.toString() + " 年 "
//        string += this.MONTH.toString() + " 月 "
//        string += this.DAY.toString() + " 日 "
        string += this.HOUR.toString() + " 時 "
        string += this.MIN.toString() + "分"

        return string
    }
}