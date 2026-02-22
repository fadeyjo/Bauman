package com.example.data_provider_app.ui.Splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.data_provider_app.jwt.TokenStorage
import com.example.data_provider_app.ui.Main.MainActivity
import com.example.data_provider_app.ui.SignIn.SignInActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenStorage.init(applicationContext)

        lifecycleScope.launch {
            val refreshToken = TokenStorage.getRefreshToken()

            if (refreshToken.isNullOrEmpty()) {
                navigateToSignIn()
            }
            else {
                observeViewModel()
                viewModel.checkToken(refreshToken)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    SplashState.NetworkError -> {
                        showError("Нет подключения к интернету")
                    }

                    SplashState.NotAuthorized -> {
                        navigateToSignIn()
                        TokenStorage.clear()
                        finish()
                    }

                    SplashState.ServerError -> {
                        showError("Ошибка сервера")
                    }

                    SplashState.UnknownError -> {
                        showError("Неизвестная ошибка")
                    }

                    SplashState.ValidationError -> {
                        showError("Ошибка валидации")
                    }

                    SplashState.Authorized -> {
                        navigateToMain()
                        finish()
                    }

                    SplashState.Loading -> {}
                }
            }
        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("ОК") { _, _ ->
                navigateToSignIn()
            }
            .setCancelable(false)
            .show()
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
