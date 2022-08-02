package com.example.gerdapp.ui

import com.example.gerdapp.ui.Time.date
import com.example.gerdapp.ui.Time.hour
import com.example.gerdapp.ui.Time.min
import com.example.gerdapp.ui.Time.month
import com.example.gerdapp.ui.Time.sec
import com.example.gerdapp.ui.Time.year
import java.util.*

object Time {
    var year: Int = 0
    var month: Int = 0
    var date: Int = 0
    var hour: Int = 0
    var min: Int = 0
    var sec: Int = 0
}

fun resetTime() {
    year = 0
    month = 0
    date = 0
    hour = 0
    min = 0
    sec = 0
}

fun initTime(calendar: Calendar){
    year = calendar[Calendar.YEAR]
    month = calendar[Calendar.MONTH]
    date = calendar[Calendar.DAY_OF_MONTH]
    hour = calendar[Calendar.HOUR_OF_DAY]
    min = calendar[Calendar.MINUTE]
    sec = calendar[Calendar.SECOND]
}