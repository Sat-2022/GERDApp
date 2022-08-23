package com.example.gerdapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gerdapp.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_main,
                R.id.navigation_calendar,
                R.id.navigation_chart,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.apply {
            navView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.bottom_nav_home -> {
                        navController.navigate(R.id.navigation_main)
                        true
                    }
                    R.id.bottom_nav_list -> {
                        navController.navigate(R.id.navigation_calendar)
                        true
                    }
                    R.id.bottom_nav_stat -> {
                        navController.navigate(R.id.navigation_chart)
                        true
                    }
                    R.id.bottom_nav_prof -> {
                        navController.navigate(R.id.navigation_profile)
                        true
                    }
                    else -> true
                }
            }

            val calendar = Calendar.getInstance()
            var greets: String? = null

            greets = if(calendar[Calendar.HOUR_OF_DAY] in 5..10) "早安"
                        else if(calendar[Calendar.HOUR_OF_DAY] in 11..17) "午安"
                        else if(calendar[Calendar.HOUR_OF_DAY] in 18..24 && calendar[Calendar.HOUR_OF_DAY] in 0..4) "晚安"
                        else "您好"

            toolbar.title = "${calendar[Calendar.MONTH]+1} 月 ${calendar[Calendar.DAY_OF_MONTH]} 日，星期" +
                    when(calendar[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> "一"
                        Calendar.TUESDAY -> "二"
                        Calendar.WEDNESDAY -> "三"
                        Calendar.THURSDAY -> "四"
                        Calendar.FRIDAY -> "五"
                        Calendar.SATURDAY -> "六"
                        else -> "日"
                    }

            val preferences: SharedPreferences = getSharedPreferences("config", 0)
            val userName = preferences.getString("nickname", "")

            when(preferences.getString("gender", "")) {
                "男" -> appBarTitle.text = "${userName}先生，${greets}！"
                "女" -> appBarTitle.text = "${userName}女士，${greets}！"
                else -> appBarTitle.text = "${userName}，${greets}！"
            }
        }


//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)

    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navController.navigateUp(appBarConfiguration)
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.navView.visibility = visibility
    }

    fun setActionBarExpanded(expanded: Boolean) {
        binding.appBar.setExpanded(expanded)
    }
}