package com.example.data_provider_app.ui.SignIn

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.data_provider_app.R
import com.example.data_provider_app.ui.Main.MainActivity
import com.example.data_provider_app.util.UserPreferences
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.data_provider_app.ui.SignUp.SignUpActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnSignIn: Button
    private lateinit var tvRegistration: TextView

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }

        tvRegistration = findViewById(R.id.tvRegistration)
        tvRegistration.paintFlags =
            tvRegistration.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        btnSignIn = findViewById(R.id.btnSignIn)

        observeViewModel()

        btnSignIn.setOnClickListener { signIn() }

        tvRegistration.setOnClickListener { signUp() }
    }

    private fun signUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun signIn() {
        viewModel.resetState()

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
        else {
            tilPassword.error = null
        }

        if (isValid)
            viewModel.signIn(email, password)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->

                when (state) {
                    is SignInState.Loading -> {
                        btnSignIn.isEnabled = false
                    }

                    is SignInState.Success -> {
                        UserPreferences.saveEmail(this@SignInActivity, etEmail.text.toString())
                        UserPreferences.savePassword(this@SignInActivity, etPassword.text.toString())
                        navigateToMain()
                    }

                    is SignInState.EmailError -> {
                        tilEmail.error = state.message
                        btnSignIn.isEnabled = true
                    }

                    is SignInState.PasswordError -> {
                        tilPassword.error = state.message
                        btnSignIn.isEnabled = true
                    }

                    is SignInState.GeneralError -> {
                        showErrorDialog(state.message)
                        btnSignIn.isEnabled = true
                    }

                    SignInState.Idle -> {
                        btnSignIn.isEnabled = true
                    }

                    is SignInState.ValidationError -> {
                        state.map["Email"]?.let { error ->
                            tilEmail.error = error
                        }

                        state.map["Password"]?.let { error ->
                            tilPassword.error = error
                        }

                        btnSignIn.isEnabled = true
                    }
                }
            }
        }
    }
}