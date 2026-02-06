package com.example.data_provider_app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.data_provider_app.util.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val email = UserPreferences.getEmail(this@SplashActivity).first()

            if (!email.isNullOrEmpty())
            {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            else
            {
                startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            }

            finish()
        }
    }
}