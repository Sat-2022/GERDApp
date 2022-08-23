package com.example.gerdapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gerdapp.databinding.ActivityLoginBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    val MIN_PASSWORD_LENGTH = 6

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

            }
        }
    }


    // Checking if the input in form is valid
    private fun validateInput(): Boolean {
        binding.apply {
            // TODO: Handle login event here
//            if (etAccount.text.toString() == "") {
//                etAccount.error = "Please Enter Email"
//                return false
//            }
//            if (etPassword.text.toString() == "") {
//                etPassword.error = "Please Enter Password"
//                return false
//            }
//
//            // checking the proper email format
//            if (!isEmailValid(etAccount.text.toString())) {
//                etAccount.error = "Please Enter Valid Email"
//                return false
//            }
//
//            // checking minimum password Length
//            if (etPassword.text.length < MIN_PASSWORD_LENGTH) {

//                etPassword.error = "Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters"
//                return false
//            }
            return true
        }
    }

    private fun isEmailValid(email: String?): Boolean {
        return true //Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Hook Click Event
    private fun performLogin() {
        if (validateInput()) {
            val account: String?
            val password: String?

            binding.apply {
                account = "T010" // etAccount!!.text.toString()
                password = "1234" // etPassword!!.text.toString()

                editor.putString("account", account)
                editor.putString("password", password)

                // TODO: Get user information from API
                getUserApi(account).start()
                editor.putString("nickname", "王小明")
//                editor.putString("gender", "男")
                editor.putBoolean("loggedIn", true)

                editor.commit()
            }

            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()
            // Here you can call you API
            // Check this tutorial to call server api through Google Volley Library https://handyopinion.com
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    private fun getUserApi(account: String): Thread {
        return Thread {
            val url = URL(getString(R.string.get_user_data_url, getString(R.string.server_url), account))
            val connection = url.openConnection() as HttpURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val type: java.lang.reflect.Type? = object : TypeToken<List<UserData>>() {}.type
                val userData: List<UserData> = Gson().fromJson(inputStreamReader, type)

                try {
                    val user: UserData = userData.first()
                    editor.putString("gender", user.Gender)
                    editor.commit()
                } catch (e: Exception) {
                    // TODO: Catch exception when no data
                }

                inputStreamReader.close()
                inputSystem.close()
                Log.e("API Connection", "user data: $userData")
            } else
                Log.e("API Connection", "failed")
        }
    }
}