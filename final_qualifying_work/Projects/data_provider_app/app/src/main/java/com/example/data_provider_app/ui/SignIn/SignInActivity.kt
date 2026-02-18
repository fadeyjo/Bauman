package com.example.data_provider_app.ui.SignIn

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.data_provider_app.R
import com.example.data_provider_app.ui.Main.MainActivity
import com.example.data_provider_app.util.UserPreferences
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import kotlin.getValue

class SignInActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnSignIn: Button

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        etEmail = findViewById<EditText>(R.id.etEmail)
        etPassword = findViewById<EditText>(R.id.etPassword)
        tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        btnSignIn = findViewById<Button>(R.id.btnSignIn)

        observeViewModel()

        btnSignIn.setOnClickListener {
            signIn()
        }
    }

    fun signIn() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        var isValid = true

        if (email.isBlank()) {
            tilEmail.error = "Введите email"
            isValid = false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Некорректный формат email"
            isValid = false
        }
        else {
            tilEmail.error = null
        }

        if (password.isBlank()) {
            tilPassword.error = "Введите пароль"
            isValid = false
        }
        else if (password.length < 8 || password.length > 32) {
            tilPassword.error = "Длина пароля от 8 до 32 символов"
            isValid = false
        }
        else {
            tilPassword.error = null
        }

        if (isValid)
            viewModel.getPersonByEmail(email)
    }

    fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.getPersonState.collect { state ->
                when (state) {
                    is GetPersonState.Data -> {
                        viewModel.checkPassword(etEmail.text.toString(), etPassword.text.toString())
                    }
                    is GetPersonState.Error -> {  }
                    is GetPersonState.Loading -> {  }
                    is GetPersonState.NetworkError -> {  }
                    is GetPersonState.NotFound -> tilEmail.error = "Пользователя с данным email не существует"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.checkPasswordState.collect { state ->
                when (state) {
                    is PasswordCheckState.Authorized -> {
                        UserPreferences.saveEmail(this@SignInActivity, etEmail.text.toString())
                        UserPreferences.savePassword(this@SignInActivity, etPassword.text.toString())

                        startActivity(
                            Intent(this@SignInActivity, MainActivity::class.java)
                        )

                        finish()
                    }
                    is PasswordCheckState.Error -> {  }
                    is PasswordCheckState.Loading -> {  }
                    is PasswordCheckState.NetworkError -> {  }
                    is PasswordCheckState.Unauthorized -> tilPassword.error = "Неправильный пароль"
                }
            }
        }
    }
}