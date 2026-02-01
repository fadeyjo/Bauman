package com.example.request_service_app

import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    lateinit var tilEmail: TextInputLayout
    lateinit var tilPassword: TextInputLayout
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvRegistration = findViewById<TextView>(R.id.tvRegistrtion)
        tvRegistration.paintFlags = tvRegistration.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)

        etEmail = findViewById<EditText>(R.id.etEmail)
        etPassword = findViewById<EditText>(R.id.etPassword)

        btnLogIn = findViewById<Button>(R.id.btnLogIn)

        btnLogIn.setOnClickListener {
            logIn()
        }
    }

    fun logIn() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.isBlank()) {
            tilEmail.error = if (email.isBlank()) "Введите email" else null
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Невалидный формат email"
        }
        else {
            tilEmail.error = null
        }

        if (password.isBlank()) {
            tilPassword.error = if (password.isBlank()) "Введите пароль" else null
        }
        else {
            tilPassword.error = null
        }
    }
}