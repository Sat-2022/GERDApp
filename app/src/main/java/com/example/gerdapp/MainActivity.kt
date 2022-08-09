package com.example.gerdapp

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
            toolbar.title = "8 月 8 日，星期一"
        }
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