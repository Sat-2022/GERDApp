package com.example.gerdapp.data

import java.util.*

/**********************************************
 * A data structure to maintain a time record.
 * Contains date, time, and day of week.
 **********************************************/
class TimeRecord {
    var YEAR = 1911
    var MONTH = 1
    var DAY = 1
    var HOUR = 0
    var MIN = 0
    var SEC = 0
    var DAY_OF_WEEK = 0

    /*
     * This is a function to set the time record with the given date and time.
     */
    fun setTimeRecord(year: Int, month: Int, day: Int, hour: Int, min: Int, sec: Int) {
        YEAR = year
        MONTH = month
        DAY = day
        HOUR = hour
        MIN = min
        SEC = sec
    }

    /*
     * This is a function to check if the time record is empty.
     * If the time record equals 1911/1/1 00:00:00, then it's empty.
     * Else, its not.
     */
    fun isTimeRecordEmpty(): Boolean {
        if(YEAR == 1911 && MONTH == 1 && DAY == 1 && HOUR == 0 && MIN == 0 && SEC == 0) return true
        return false
    }

    /*
     * This is a function that convert the given string to a time record.
     * The string must be in the format of yyyy-mm-ddTHH:mm:ss.ss.
     */
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

    /*
     * This is a function that check if the time record is at the same date as the current date.
     */
    private fun isToday(): Boolean {
        val calendar = Calendar.getInstance()

        if(this.YEAR != calendar[Calendar.YEAR]) return false
        if(this.MONTH != calendar[Calendar.MONTH]+1) return false
        if(this.DAY != calendar[Calendar.DAY_OF_MONTH]) return false

        return true
    }

    /*
     * This is a function that check if the time record is at the same date as the given date.
     */
    fun isEqual(calendar: Calendar): Boolean {
        if(this.YEAR != calendar[Calendar.YEAR]) return false
        if(this.MONTH != calendar[Calendar.MONTH]+1) return false
        if(this.DAY != calendar[Calendar.DAY_OF_MONTH]) return false

        return true
    }

    /*
     * This is a function that check if the time record is at the same date as the given time record.
     */
    fun isEqual(timeRecord: TimeRecord): Boolean {
        if(this.YEAR != timeRecord.YEAR) return false
        if(this.MONTH != timeRecord.MONTH) return false
        if(this.DAY != timeRecord.DAY) return false

        return true
    }

    /*
     * This is a function that compares to given string which are in the format of yyyy-mm-ddTHH:mm:ss.ss.
     * If the end time is after the start time, then returns true. Else, it returns false.
     */
    fun isAfter(start: String, end: String): Boolean {
        val startTimeRecord = this.stringToTimeRecord(start)
        val endTimeRecord = this.stringToTimeRecord(end)

        if(startTimeRecord.YEAR > endTimeRecord.YEAR) {
            return false
        } else if(startTimeRecord.YEAR == endTimeRecord.YEAR) {
            if(startTimeRecord.MONTH > endTimeRecord.MONTH) {
                return false
            } else if(startTimeRecord.MONTH == endTimeRecord.MONTH) {
                return if(startTimeRecord.DAY > endTimeRecord.DAY) {
                    false
                } else if (startTimeRecord.DAY == endTimeRecord.DAY) {
                    if(startTimeRecord.HOUR > endTimeRecord.HOUR) {
                        false
                    } else if(startTimeRecord.HOUR == endTimeRecord.HOUR) {
                        startTimeRecord.MIN < endTimeRecord.MIN
                    } else {
                        true
                    }
                } else {
                    true
                }
            } else {
                return true
            }
        } else {
            return true
        }
    }

    /*
     * This is a function that overrides toString(), which convert the time record to a string.
     * If the time record is at today, then the format will be XX 時 XX 分.
     * If not, the format will be XX 月 XX 日
     */
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

    /*
     * This is a function that convert the time record to a string.
     * If the flag is set to 0, then return the overridden toString() function
     * If the flag is set to 1, return the string in the format of XX 月 XX 日
     * If the flag is set to 2, return the string in the format of XX 時 XX 分
     */
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

    /*
     * This is a function that convert the time record to float.
     * The time record is rescaled to fit the decimal numbers
     */
    fun timeRecordToFloat(): Float {
        return (this.HOUR*10000 + this.MIN*10000/60 + this.SEC*100/60).toFloat()
    }

}