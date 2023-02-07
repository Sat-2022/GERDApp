package com.example.gerdapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class BootReceiver : BroadcastReceiver() {
    /*
    * restart reminders alarms when user's device reboots
    * */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val calendar = Calendar.getInstance()
            if(calendar[Calendar.HOUR_OF_DAY] in 5..14) {
                BasicApplication.RemindersManager.startReminder(context, context.getString(R.string.morning_reminder_time))
            } else {
                BasicApplication.RemindersManager.startReminder(context, context.getString(R.string.evening_reminder_time))
            }
        }
    }
}