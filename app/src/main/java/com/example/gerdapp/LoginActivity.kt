package com.example.gerdapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gerdapp.data.UserData
import com.example.gerdapp.databinding.ActivityLoginBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    val MAX_PASSWORD_LENGTH = 12

    private var isMinimized = false

    data class LoginResult(
        val ResultContent: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set preferences
        preferences = getSharedPreferences("config", MODE_PRIVATE)
        editor = preferences.edit()

        binding.apply {
            btLogin.setOnClickListener {
                performLogin()
            }
            btSignup.setOnClickListener {
                goToSignup()
            }
            btForgotPassword.setOnClickListener {
                performForgotPassword()
            }
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


    private fun performForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }


    // Checking if the input in form is valid
    private fun validateInput(): Boolean {
        binding.apply {
            // TODO: Handle login event here
            if (etAccount.text.toString() == "") {
                etAccount.error = getString(R.string.error_enter_account)
                return false
            }
            if (etPassword.text.toString() == "") {
                etPassword.error = getString(R.string.error_enter_password)
                return false
            }

            // checking the proper email format
            if (!isValidString(etAccount.text.toString())) {
                etAccount.error = getString(R.string.error_invalid_input)
                return false
            }

            if(!isValidString(etPassword.text.toString())) {
                etPassword.error = getString(R.string.error_invalid_input)
                return false
            }

            // checking minimum password Length
            if (etPassword.text.length > MAX_PASSWORD_LENGTH) {
                etPassword.error = getString(R.string.error_wrong_password)
                return false
            }
            return true
        }
    }

    private fun isValidString(string: String): Boolean {
        if(string.contains(getString(R.string.check_input_valid).toRegex())) {
            return false
        }
        return true
    }

    // Hook Click Event
    private fun performLogin() {
        if (validateInput()) {
            val account: String?
            val password: String?

            binding.apply {
                account = etAccount.text.toString()
                password = etPassword.text.toString()

//                editor.putString("account", account)
//                editor.putString("password", password)

                // TODO: Get user information from API
                getLoginStatusApi(account, password).start()

            }

            // Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()
            // Here you can call you API
            // Check this tutorial to call server api through Google Volley Library https://handyopinion.com

        } else {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToSignup() {
        // Open your SignUp Activity if the user wants to signup
        // Visit this article to get SignupActivity code https://handyopinion.com/signup-activity-in-android-studio-kotlin-java/
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun getLoginStatusApi(account: String, password: String): Thread {
        return Thread {
            try {
                val url = URL(getString(R.string.login_url, getString(R.string.server_url), account, password))
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object: TypeToken<List<LoginResult>>() {}.type
                    val list: List<LoginResult> = Gson().fromJson(inputStreamReader, type)

                    val result = list.first()
                    updateUi(result)

                    inputStreamReader.close()
                    inputSystem.close()

                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: FileNotFoundException) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

    private fun getUserInfoApi(): Thread {
        return Thread {
            try {
                val caseNumber = preferences.getString("caseNumber", "").toString()
                val url = URL(getString(R.string.get_user_url, getString(R.string.server_url), caseNumber))
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val type: java.lang.reflect.Type? = object: TypeToken<List<UserData>>() {}.type
                    val list: List<UserData> = Gson().fromJson(inputStreamReader, type)

                    val result = list.first()

                    editor.putString("nickname", result.Nickname)
                    when(result.Gender) {
                        "1" -> editor.putString("gender", "男")
                        "2" -> editor.putString("gender", "女")
                    }

                    editor.commit()

                    inputStreamReader.close()
                    inputSystem.close()

                    Log.e("API Connection", "Connection success")
                } else {
                    Log.e("API Connection", "Connection failed")
                }
            } catch (e: FileNotFoundException) {
                Log.e("API Connection", "Service not found")
            }
        }
    }

    private fun updateUi(result: LoginResult) {
        this.runOnUiThread {
            binding.apply {
                if(result.ResultContent == "1") {
                    Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()

                    editor.putString("caseNumber", etAccount.text.toString())

                    editor.putBoolean("loggedIn", true)
                    editor.putBoolean("showNotification", true)

                    getUserInfoApi().start()

                    editor.commit()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e("API Connection", "$result")
                    Toast.makeText(this@LoginActivity, R.string.login_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}