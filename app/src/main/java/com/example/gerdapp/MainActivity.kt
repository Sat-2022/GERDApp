package com.example.gerdapp

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gerdapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navController.navigateUp(appBarConfiguration)
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.navView.visibility = visibility
    }
}