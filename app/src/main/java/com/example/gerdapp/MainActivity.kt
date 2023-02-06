package com.example.gerdapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gerdapp.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    private var isMinimized = false

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

            toolbarLayout.isTitleEnabled = false

            val calendar = Calendar.getInstance()
            var greets: String? = null

            greets = if(calendar[Calendar.HOUR_OF_DAY] in 5..10) "早安"
            else if(calendar[Calendar.HOUR_OF_DAY] in 11..17) "午安"
            else if(calendar[Calendar.HOUR_OF_DAY] in 18..24 && calendar[Calendar.HOUR_OF_DAY] in 0..4) "晚安"
            else "您好"

            val preferences: SharedPreferences = getSharedPreferences("config", 0)
            val userName = preferences.getString("nickname", "")

            when(preferences.getString("gender", "")) {
                "1" -> appBarTitle.text = "$userName 先生，$greets！"
                "2" -> appBarTitle.text = "$userName 女士，$greets！"
                else -> appBarTitle.text = "$userName，$greets！"
            }
        }

//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)

        createNotificationChannel()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "" // getString(R.string.msg_token_fmt, token)
            Log.d("MainActivity", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navController.navigateUp(appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//
//        if(networkInfo == null || !networkInfo.isAvailable) {
//            startActivity(Intent(this, SplashActivity::class.java))
//            finish()
//        }

        return when (item.itemId) {
            R.id.menu_settings -> {
                navController.navigate(R.id.navigation_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        if(isMinimized) {
            startActivity(Intent(this, SplashActivity::class.java))
            isMinimized = false
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        isMinimized = true
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.navView.visibility = visibility
    }

    fun setActionBarExpanded(expanded: Boolean) {
        binding.appBar.setExpanded(expanded)
    }

    fun setActionBarTitle() {
        binding.apply {

            val calendar = Calendar.getInstance()
            var title: String? = null

            title = "${calendar[Calendar.MONTH]+1} 月 ${calendar[Calendar.DAY_OF_MONTH]} 日，星期" +
                    when(calendar[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> "一"
                        Calendar.TUESDAY -> "二"
                        Calendar.WEDNESDAY -> "三"
                        Calendar.THURSDAY -> "四"
                        Calendar.FRIDAY -> "五"
                        Calendar.SATURDAY -> "六"
                        else -> "日"
                    }


            toolbar.title = title
        }
    }

    fun setActionBarTitleEnable(boolean: Boolean) {
        binding.apply {
            toolbarLayout.isTitleEnabled = boolean
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

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}