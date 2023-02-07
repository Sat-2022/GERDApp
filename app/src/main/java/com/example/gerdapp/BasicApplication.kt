package com.example.gerdapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.gerdapp.ui.profile.ProfileFragment
import java.util.*

class BasicApplication: Application() {

    private var sendNotification: ProfileFragment.RemindCheck?= null

    object RemindersManager {
        const val REMINDER_NOTIFICATION_REQUEST_CODE = 123
        fun startReminder(
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
                        PendingIntent.FLAG_UPDATE_CURRENT
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

        fun stopReminder(
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
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.e("Reminder", "set")

        notificationOn()
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

    fun notificationOn() {
        val calendar = Calendar.getInstance()
        if(calendar[Calendar.HOUR_OF_DAY] in 5..14) {
            RemindersManager.startReminder(this, getString(R.string.morning_reminder_time))
        } else {
            RemindersManager.startReminder(this, getString(R.string.evening_reminder_time))
        }
    }

    fun notificationOff() {
        RemindersManager.stopReminder(this)
    }
}