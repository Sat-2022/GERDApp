package com.example.gerdapp.ui.profile

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.gerdapp.*
import com.example.gerdapp.databinding.FragmentProfileBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationViewVisibility = View.VISIBLE
    private var actionbarTitleEnable = true

    private lateinit var preferences: SharedPreferences

    object User {
        var caseNumber = ""
        var gender = ""
        var nickname = ""
    }

    data class RemindCheck(
        val ResultContent: String
    )

    private var sendNotification: RemindCheck ?= null

    private fun setBottomNavigationVisibility() {
        val mainActivity = activity as MainActivity
        mainActivity.setBottomNavigationVisibility(bottomNavigationViewVisibility)

        mainActivity.setActionBarTitleEnable(actionbarTitleEnable)
        mainActivity.setActionBarTitle()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationVisibility()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        preferences = requireActivity().getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE)
        User.caseNumber = preferences.getString("caseNumber", "").toString()
        User.gender = preferences.getString("gender", "").toString()
        User.nickname = preferences.getString("nickname", "").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            testButton.setOnClickListener {
                val preferences: SharedPreferences = context?.getSharedPreferences("config", 0)!!
                val editor: SharedPreferences.Editor = preferences.edit()

                editor.putBoolean("loggedIn", false)

                editor.commit()

                val intent = Intent(requireContext(), SplashActivity::class.java)
                startActivity(intent)
            }

            tvUserAcc.text = "\t案號：" + User.caseNumber
            tvUserNickname.text = "\t暱稱：" + User.nickname
            if(User.gender == "1") tvUserGender.text = "\t性別：男"
            else if(User.gender == "2") tvUserGender.text = "\t性別：女"
            else tvUserGender.text = "\t性別："


            notificationButton.setOnClickListener {
                getRemindCheckApi().start()
            }
        }
    }

    private fun updateRemindCheck() {
        activity?.runOnUiThread {
            when (sendNotification!!.ResultContent) {
                "1" -> {
                    val intent = Intent(requireContext(), SplashActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

                    var builder = NotificationCompat.Builder(requireContext(), "default")
                        .setSmallIcon(R.drawable.ic_baseline_list_24)
                        .setContentTitle("填寫問卷通知")
                        .setContentText("記得填寫問卷喔！")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    with(NotificationManagerCompat.from(requireContext())) {
                        notify(1, builder.build())
                    }
                }
                else -> {
//                    val intent = Intent(requireContext(), SplashActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                    val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//                    var builder = NotificationCompat.Builder(requireContext(), "default")
//                        .setSmallIcon(R.drawable.ic_baseline_list_24)
//                        .setContentTitle("填寫問卷通知")
//                        .setContentText("記得填寫問卷喔！")
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
//
//                    with(NotificationManagerCompat.from(requireContext())) {
//                        notify(1, builder.build())
//                    }
                }
            }
        }
    }

    private fun getRemindCheckApi(): Thread {
        return Thread {
            try {
                val url = URL(getString(
                    R.string.get_remind_check_url,
                    getString(R.string.server_url),
                    User.caseNumber,
                    "B"
                ))
                val connection = url.openConnection() as HttpURLConnection
                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? =
                        object : TypeToken<List<RemindCheck>>() {}.type
                    val list: List<RemindCheck> = Gson().fromJson(inputStreamReader, type)

                    sendNotification = list.first()
                    updateRemindCheck()

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
}