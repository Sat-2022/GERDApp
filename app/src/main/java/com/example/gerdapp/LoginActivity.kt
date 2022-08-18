package com.example.gerdapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gerdapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    val MIN_PASSWORD_LENGTH = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    // Checking if the input in form is valid
    fun validateInput(): Boolean {
        binding.apply {
            if (etEmail.text.toString() == "") {
                etEmail.error = "Please Enter Email"
                return false
            }
            if (etPassword.text.toString() == "") {
                etPassword.error = "Please Enter Password"
                return false
            }

            // checking the proper email format
            if (!isEmailValid(etEmail.text.toString())) {
                etEmail.error = "Please Enter Valid Email"
                return false
            }

            // checking minimum password Length
            if (etPassword.text.length < MIN_PASSWORD_LENGTH) {
                etPassword.error = "Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters"
                return false
            }
            return true
        }
    }

    fun isEmailValid(email: String?): Boolean {
        return true //Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Hook Click Event
    fun performSignUp(v: View) {
        if (validateInput()) {
            // input is valid, here send data to your server
            val email: String?
            val password: String?

            binding.apply {
                email = etEmail!!.text.toString()
                password = etPassword!!.text.toString()
            }

            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
            // Here you can call you API
            // Check this tutorial to call server api through Google Volley Library https://handyopinion.com


            if (email != null) {
                setUserData("2", email, "王先生", "男")
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun goToSignup(v: View) {
        // Open your SignUp Activity if the user wants to signup
        // Visit this article to get SignupActivity code https://handyopinion.com/signup-activity-in-android-studio-kotlin-java/
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}