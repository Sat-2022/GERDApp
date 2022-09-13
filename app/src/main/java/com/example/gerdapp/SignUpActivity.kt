package com.example.gerdapp

import android.content.Intent
import android.content.SharedPreferences
import android.opengl.ETC1.isValid
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.gerdapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    val MIN_PASSWORD_LENGTH = 6;
    val MAX_PASSWORD_LENGTH = 12

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
            if (etNickname.text.toString() == "") {
                etNickname.error = getString(R.string.error_enter_nickname)
                return false
            }
            if (etGender.text.toString() == "") {
                etGender.error = getString(R.string.error_enter_gender)
                return false
            }
            if (etEmail.text.toString() == "") {
                etEmail.error = getString(R.string.error_enter_account)
                return false
            }
            if (etPassword.text.toString() == "") {
                etPassword.error = getString(R.string.error_enter_password)
                return false
            }
            if (etCheckPassword.text.toString() == "") {
                etCheckPassword.error = getString(R.string.error_check_password_failed)
                return false
            }

            // check nickname input
            if (isInvalidString(etNickname.text.toString())) {
                etNickname.error = getString(R.string.error_invalid_input)
                return false
            }

            // checking gender input
            if (isInvalidString(etGender.text.toString())) {
                etGender.error = getString(R.string.error_invalid_input)
                return false
            }

            // checking email input
            if (isInvalidString(etEmail.text.toString())) {
                etEmail.error = getString(R.string.error_invalid_input)
                return false
            }
            if (!isEmailValid(etEmail.text.toString())) {
                etEmail.error = getString(R.string.error_illegal_account)
                return false
            }

            // checking password input
            if(isInvalidString(etPassword.text.toString())) {
                etPassword.error = getString(R.string.error_invalid_input)
                return false
            }
            if (etPassword.text.length < MIN_PASSWORD_LENGTH || etPassword.text.length > MAX_PASSWORD_LENGTH) {
                etPassword.error = getString(R.string.error_illegal_password_length)
                return false
            }
            if(!isPasswordValid(etPassword.text.toString())) {
                etPassword.error = getString(R.string.error_illegal_password)
                return false
            }
            // Checking if repeat password is same
            if (etPassword.text.toString() != etCheckPassword.text.toString()) {
                etCheckPassword.error = getString(R.string.error_check_password_failed)
                return false
            }
            return true
        }
    }

    private fun isEmailValid(email: String): Boolean {
        if(email[0] == 'R') return false

        return true
//        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isInvalidString(string: String): Boolean {
        if(string.contains(getString(R.string.check_input_valid).toRegex())) {
            return true
        }
        return false
    }

    private fun isPasswordValid(password: String): Boolean {
        if(password.contains(getString(R.string.check_alphabetic).toRegex())
            && password.contains(getString(R.string.check_numeric).toRegex())) {
            return true
        }
        return false
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