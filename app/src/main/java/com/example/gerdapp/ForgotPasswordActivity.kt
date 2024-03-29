package com.example.gerdapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.gerdapp.databinding.ActivityForgotPasswordBinding

/**********************************************
 * Perform forgot password
 * This activity is launched when the user pressed on forgot-password button in login page
 **********************************************/
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private val VERIFICATION_CODE_LENGTH = 6

    private var isMinimized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btSendVerificationCode.setOnClickListener {
                getUserAccount()
                layoutVerificationCode.visibility = View.VISIBLE
                btSendVerificationCode.visibility = View.GONE
                btResendVerificationCode.visibility = View.VISIBLE
                btVerification.visibility = View.VISIBLE
            }

            btResendVerificationCode.setOnClickListener {
                sendVerificationCode()
            }

            btVerification.setOnClickListener {
                val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
                startActivity(intent)
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


    private fun getUserAccount() {
        // TODO:: find user account
        sendVerificationCode()
    }

    private fun sendVerificationCode() {
        // TODO:: perform sending verification code
    }
}