package com.example.gerdapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.example.gerdapp.BasicApplication.RemindersManager.notificationOff
import com.example.gerdapp.BasicApplication.RemindersManager.notificationOn
import com.example.gerdapp.ui.profile.ProfileFragment
import java.util.*

class BasicApplication: Application() {

    object RemindersManager {
        const val REMINDER_NOTIFICATION_REQUEST_CODE = 123
        private fun startReminder(
            context: Context,
            reminderTime: String = "08:00",
            reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val (hours, min) = reminderTime.split(":").map { it.toInt() }
            val intent =
                Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        reminderId,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

            val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, min)
            }

            // If the trigger time you specify is in the past, the alarm triggers immediately.
            // if soo just add one day to required calendar
            // Note: i'm also adding one min cuz if the user clicked on the notification as soon as
            // he receive it it will reschedule the alarm to fire another notification immediately
            if (Calendar.getInstance(Locale.ENGLISH)
                    .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
            ) {
                calendar.add(Calendar.DATE, 1)
            }

            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
                intent
            )
        }

        private fun stopReminder(
            context: Context,
            reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    reminderId,
                    intent,
                    0
                )
            }
            alarmManager.cancel(intent)
        }

        fun notificationOn(context: Context) {
            val calendar = Calendar.getInstance()
            if(calendar[Calendar.HOUR_OF_DAY] in 12..21) {
                startReminder(context, context.getString(R.string.evening_reminder_time))
            } else {
                startReminder(context, context.getString(R.string.morning_reminder_time))
            }
        }

        fun notificationOff(context: Context) {
            stopReminder(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val preferences: SharedPreferences = getSharedPreferences("config", 0)
        if(preferences.getString("reminder", "on") == "on") {
            notificationOn(this)
        } else {
            notificationOff(this)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default", "DemoCode", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)!!
            manager!!.createNotificationChannel(channel)
        }
    }
}