package com.example.gerdapp

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gerdapp.databinding.ActivitySplashBinding

class SplashActivity: AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000 // 1 sec

    private lateinit var binding: ActivitySplashBinding

    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connectivityManager.activeNetworkInfo

        Handler().postDelayed({
            if(networkInfo == null || !networkInfo!!.isAvailable) {
                binding.apply {
                    internetConnectFailed.visibility = View.VISIBLE
                    splash.visibility = View.GONE
                    refreshIcon.setOnClickListener {
                        networkInfo = connectivityManager.activeNetworkInfo
                        if(networkInfo == null || !networkInfo!!.isAvailable) {

                        } else {
                            completeSplash()
                        }
                    }
                }
            } else {
                completeSplash()
            }
        }, SPLASH_TIME_OUT)
    }

    private fun completeSplash() {
        binding.apply {
            internetConnectFailed.visibility = View.GONE
            splash.visibility = View.VISIBLE
        }

        var startActivity: Intent
        val preferences: SharedPreferences = getSharedPreferences("config", 0)

        Handler().postDelayed({
            startActivity = if (preferences.getBoolean("loggedIn", false)) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(startActivity)
            finish()
        }, SPLASH_TIME_OUT)
    }
}