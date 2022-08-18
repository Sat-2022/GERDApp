package com.example.gerdapp

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    // Checking if the input in form is valid
    fun validateInput(): Boolean {
        binding.apply {
            if (etFirstName.text.toString().equals("")) {
                etFirstName.setError("Please Enter First Name")
                return false
            }
            if (etLastName.text.toString().equals("")) {
                etLastName.setError("Please Enter Last Name")
                return false
            }
            if (etEmail.text.toString().equals("")) {
                etEmail.setError("Please Enter Email")
                return false
            }
            if (etPassword.text.toString().equals("")) {
                etPassword.setError("Please Enter Password")
                return false
            }
            if (etRepeatPassword.text.toString().equals("")) {
                etRepeatPassword.setError("Please Enter Repeat Password")
                return false
            }

            // checking the proper email format
            if (!isEmailValid(etEmail.text.toString())) {
                etEmail.setError("Please Enter Valid Email")
                return false
            }

            // checking minimum password Length
            if (etPassword.text.length < MIN_PASSWORD_LENGTH) {
                etPassword.setError("Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters")
                return false
            }

            // Checking if repeat password is same
            if (!etPassword.text.toString().equals(etRepeatPassword.text.toString())) {
                etRepeatPassword.setError("Password does not match")
                return false
            }
            return true
        }
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Hook Click Event

    fun performSignUp (view: View) {
        if (validateInput()) {
            // Input is valid, here send data to your server
            binding.apply {
                val firstName = etFirstName.text.toString()
                val lastName = etLastName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val repeatPassword = etRepeatPassword.text.toString()
            }

            Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show()
            // Here you can call you API
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}