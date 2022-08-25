package com.example.gerdapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.gerdapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    val MIN_PASSWORD_LENGTH = 6;

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set preferences
        preferences = getSharedPreferences("config", MODE_PRIVATE)
        editor = preferences.edit()

        binding.apply {
            btSignup.setOnClickListener {
                goToLogin()
            }
            btRegister.setOnClickListener {
                performSignUp()
            }
        }
    }


    // Checking if the input in form is valid
    private fun validateInput(): Boolean {
        binding.apply {
            // TODO: Handle register event here
//            if (etNickname.text.toString().equals("")) {
//                etNickname.setError("Please Enter First Name")
//                return false
//            }
//            if (etNickname.text.toString().equals("")) {
//                etNickname.setError("Please Enter Last Name")
//                return false
//            }
//            if (etEmail.text.toString().equals("")) {
//                etEmail.setError("Please Enter Email")
//                return false
//            }
//            if (etPassword.text.toString().equals("")) {
//                etPassword.setError("Please Enter Password")
//                return false
//            }
//            if (etCheckPassword.text.toString().equals("")) {
//                etCheckPassword.setError("Please Enter Repeat Password")
//                return false
//            }
//
//            // checking the proper email format
//            if (!isEmailValid(etEmail.text.toString())) {
//                etEmail.setError("Please Enter Valid Email")
//                return false
//            }
//
//            // checking minimum password Length
//            if (etPassword.text.length < MIN_PASSWORD_LENGTH) {
//                etPassword.setError("Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters")
//                return false
//            }
//
//            // Checking if repeat password is same
//            if (!etPassword.text.toString().equals(etCheckPassword.text.toString())) {
//                etCheckPassword.setError("Password does not match")
//                return false
//            }
            return true
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Hook Click Event

    private fun performSignUp() {
        if (validateInput()) {
            val nickname: String?
            val gender: String?
            val email: String?
            val password: String?
            val checkPassword: String?

            binding.apply {
                nickname = "陳小花" // etNickname.text.toString()
                gender = "女" // etGender.text.toString()
                email = "T010" // etEmail.text.toString()
                password = "1234" // etPassword.text.toString()
                checkPassword = "1234" // etCheckPassword.text.toString()
            }

            editor.putString("account", email)
            editor.putString("password", password)
            editor.putString("nickname", nickname)
            editor.putString("gender", gender)
            editor.putBoolean("loggedIn", true)

            editor.commit()

            Toast.makeText(this,R.string.sign_up_success,Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this,R.string.sign_up_failed,Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}