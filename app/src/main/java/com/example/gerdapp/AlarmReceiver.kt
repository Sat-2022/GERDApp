package com.example.gerdapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AlarmReceiver: BroadcastReceiver() {

    data class RemindCheck(
        val ResultContent: String
    )

    private lateinit var preferences: SharedPreferences

    object User {
        var caseNumber = ""
        var gender = ""
        var nickname = ""
    }

    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     */
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        preferences = context.getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        User.caseNumber = preferences.getString("caseNumber", "").toString()
        User.gender = preferences.getString("gender", "").toString()
        User.nickname = preferences.getString("nickname", "").toString()

        getAlarmStatusApi(context, notificationManager).start()

        // reschedule the reminder
        BasicApplication.RemindersManager.notificationOn(context)
    }
}

private fun getAlarmStatusApi(context: Context, notificationManager: NotificationManager): Thread {
    return Thread {
        try {
            Log.e("alarm", "connection")
            val url = URL(context.getString(
                R.string.get_remind_check_url,
                context.getString(R.string.server_url),
                AlarmReceiver.User.caseNumber,
                "B"
            ))
            val connection = url.openConnection() as HttpURLConnection
            Log.e("alarm", "${connection.responseCode}")
            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? =
                    object : TypeToken<List<AlarmReceiver.RemindCheck>>() {}.type
                val list: List<AlarmReceiver.RemindCheck> = Gson().fromJson(inputStreamReader, type)

                val sendNotification = list.first()
                when (sendNotification.ResultContent) {
                    "1" -> {

                        notificationManager.sendReminderNotification(
                            applicationContext = context,
                            channelId = "default"// context.getString(R.string.reminders_notification_channel_id)
                        )
                    }
                    else -> {
                        notificationManager.sendReminderNotification(
                            applicationContext = context,
                            channelId = "default"// context.getString(R.string.reminders_notification_channel_id)
                        )
                    }
                }
                inputStreamReader.close()
                inputSystem.close()

                Log.e("API Connection", "Connection success")
            } else {
                Log.e("API Connection", "Connection failed")
            }
        } catch(e: Exception) {
            Log.e("API Connection", "Service not found")
        }
    }
}

fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setContentTitle(applicationContext.getString(R.string.reminder_title))
        .setContentText(applicationContext.getString(R.string.reminder_text))
        .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
//        .setStyle(
//            NotificationCompat.BigTextStyle()
//                .bigText(applicationContext.getString(R.string.description_notification_reminder))
//        )
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

const val NOTIFICATION_ID = 1

