package com.example.gerdapp.data.model

import android.widget.Toast
import com.example.gerdapp.R
import kotlin.coroutines.coroutineContext

class TimeRecord {
    var YEAR = 1
    var MONTH = 1
    var DAY = 1
    var HOUR = 0
    var MIN = 0
    var SEC = 0
    var DAY_OF_WEEK = 1

    fun setTimeRecord(year: Int, month: Int, day: Int, hour: Int, min: Int, sec: Int) {
        YEAR = year
        MONTH = month
        DAY = day
        HOUR = hour
        MIN = min
        SEC = sec
    }

    fun isTimeRecordEmpty(): Boolean {
        if(YEAR == 1 && MONTH == 1 && DAY == 1 && HOUR == 0 && MIN == 0 && SEC == 0) return true
        return false
    }
}