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
            val email: String?
            val password: String?

            binding.apply {
                email = etAccount!!.text.toString()
                password = etPassword!!.text.toString()
                setUserData("2", "T010", "王先生", "男")
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
}