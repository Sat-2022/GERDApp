package com.example.gerdapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.*

/**********************************************
 * Handles boot event
 **********************************************/
class BootReceiver : BroadcastReceiver() {
    /*
     * restart reminders alarms when user's device reboots
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val preferences: SharedPreferences = context.getSharedPreferences("config", 0)
            if(preferences.getString("reminder", "on") == "on") {
                BasicApplication.RemindersManager.notificationOn(context)
            } else {
                BasicApplication.RemindersManager.notificationOff(context)
            }
        }
    }
}