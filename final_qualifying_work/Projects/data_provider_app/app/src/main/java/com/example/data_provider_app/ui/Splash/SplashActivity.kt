package com.example.data_provider_app.ui.Splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.data_provider_app.model.dto.CheckPasswordRequest
import com.example.data_provider_app.ui.Main.MainActivity
import com.example.data_provider_app.ui.SignIn.SignInActivity
import com.example.data_provider_app.util.ApiResult
import com.example.data_provider_app.util.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        lifecycleScope.launch {
            UserPreferences.clearEmail(this@SplashActivity)
            UserPreferences.clearPassword(this@SplashActivity)

            val email = UserPreferences.getEmail(this@SplashActivity).first()
            val password = UserPreferences.getPassword(this@SplashActivity).first()

            if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                viewModel.checkUser(email, password)
            }
            else {
                navigateToSignIn()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    SplashState.Authorized -> navigateToMain()
                    SplashState.NotAuthorized -> navigateToSignIn()
                    SplashState.Error -> navigateToSignIn()
                    SplashState.Loading -> {}
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}
