package com.example.gerdapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.gerdapp.databinding.ActivitySplashBinding

class SplashActivity: AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000 // 1 sec

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val preferences: SharedPreferences = getSharedPreferences("", MODE_PRIVATE)
//        preferences.getString("")


        var startActivity: Intent

        Handler().postDelayed({
            if(false) {
                startActivity = Intent(this, MainActivity::class.java)
            } else {
                startActivity = Intent(this, LoginActivity::class.java)
            }

            startActivity(startActivity)
            finish()
        }, SPLASH_TIME_OUT)

    }
}