package com.example.data_provider_app.ui.SignUp

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.data_provider_app.R
import com.example.data_provider_app.ui.Main.MainActivity
import com.example.data_provider_app.ui.SignIn.SignInActivity
import com.google.android.material.textfield.TextInputLayout

class SignUpActivity : AppCompatActivity() {
    private lateinit var tvSignIn: TextView

    private lateinit var etEmail: EditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etPhone: EditText
    private lateinit var tilPhone: TextInputLayout
    private lateinit var etLastName: EditText
    private lateinit var tilLastName: TextInputLayout
    private lateinit var etFirstName: EditText
    private lateinit var tilFirstName: TextInputLayout
    private lateinit var etPatronymic: EditText
    private lateinit var tilPatronymic: TextInputLayout
    private lateinit var etBirth: EditText
    private lateinit var tilBirth: TextInputLayout
    private lateinit var etDriveLicense: EditText
    private lateinit var tilDriveLicense: TextInputLayout
    private lateinit var etPassword: EditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etRepeatPassword: EditText
    private lateinit var tilRepeatPassword: TextInputLayout
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

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

        tvSignIn = findViewById(R.id.tvSignIn)
        tvSignIn.paintFlags =
            tvSignIn.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        etEmail = findViewById(R.id.etEmail)
        tilEmail = findViewById(R.id.tilEmail)
        etPhone = findViewById(R.id.etPhone)
        tilPhone = findViewById(R.id.tilPhone)
        etLastName = findViewById(R.id.etLastName)
        tilLastName = findViewById(R.id.tilLastName)
        etFirstName = findViewById(R.id.etFirstName)
        tilFirstName = findViewById(R.id.tilFirstName)
        etPatronymic = findViewById(R.id.etPatronymic)
        tilPatronymic = findViewById(R.id.tilPatronymic)
        etBirth = findViewById(R.id.etBirth)
        tilBirth = findViewById(R.id.tilBirth)
        etDriveLicense = findViewById(R.id.etDriveLicense)
        tilDriveLicense = findViewById(R.id.tilDriveLicense)
        etPassword = findViewById(R.id.etPassword)
        tilPassword = findViewById(R.id.tilPassword)
        etRepeatPassword = findViewById(R.id.etConfirmPassword)
        tilRepeatPassword = findViewById(R.id.tilConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)

        tvSignIn.setOnClickListener { signIn() }

        btnSignUp.setOnClickListener { signUp() }

        etBirth.setOnClickListener {
            val calendar = Calendar.getInstance()

            val today = Calendar.getInstance()
            today.add(Calendar.YEAR, -18)
            today.add(Calendar.DAY_OF_MONTH, -1)

            val year = today.get(Calendar.YEAR)
            val month = today.get(Calendar.MONTH)
            val day = today.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%02d.%02d.%d", selectedDay, selectedMonth + 1, selectedYear)
                    etBirth.setText(formattedDate)
                }, year, month, day
            )

            datePicker.datePicker.maxDate = today.timeInMillis

            datePicker.show()
        }
    }

    private fun signUp() {
        val email = etEmail.text.toString()
        val phone = etPhone.text.toString()
        val lastName = etLastName.text.toString()
        val firstName = etFirstName.text.toString()
        val patronymic = etPatronymic.text.toString()
        val birth = etBirth.text.toString()
        val driveLicense = etDriveLicense.text.toString()
        val password = etPatronymic.text.toString()
        val repeatPassword = etRepeatPassword.text.toString()

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

        val phoneRegex1 = Regex("""^\+7\d*$""")
        val phoneRegex2 = Regex("""^\+7\d{10}$""")
        if (phone.isBlank()) {
            tilPhone.error = "Введите номер телефона"
            isValid = false
        }
        else if (!phoneRegex1.matches(phone)) {
            tilPhone.error = "Номер телефона должен состоять из +7 и цифр"
            isValid = false
        }
        else if (!phoneRegex2.matches(phone)) {
            tilPhone.error = "Номер телефона должен содержать 11 цифр"
            isValid = false
        }
        else {
            tilPhone.error = null
        }

        val namesRegex1 = Regex("""^[А-ЯЁа-яё]*$""")
        val namesRegex2 = Regex("""^[А-ЯЁ][а-яё]*$""")
        val namesRegex3 = Regex("""^[А-ЯЁ][а-яё]{1,49}$""")

        if (lastName.isBlank()) {
            tilLastName.error = "Введите фамилию"
            isValid = false
        }
        else if (!namesRegex1.matches(lastName)) {
            tilLastName.error = "Фамилия должна состоять только из букв"
            isValid = false
        }
        else if (!namesRegex2.matches(lastName)) {
            tilLastName.error = "Первая буква - строчная, остальные - прописные"
            isValid = false
        }
        else if (!namesRegex3.matches(lastName)) {
            tilLastName.error = "Минимальная длина 2, максимальная - 50"
            isValid = false
        }
        else {
            tilLastName.error = null
        }

        if (firstName.isBlank()) {
            tilFirstName.error = "Введите имя"
            isValid = false
        }
        else if (!namesRegex1.matches(firstName)) {
            tilFirstName.error = "Имя должно состоять только из букв"
            isValid = false
        }
        else if (!namesRegex2.matches(firstName)) {
            tilFirstName.error = "Первая буква - строчная, остальные - прописные"
            isValid = false
        }
        else if (!namesRegex3.matches(firstName)) {
            tilFirstName.error = "Минимальная длина 2, максимальная - 50"
            isValid = false
        }
        else {
            tilFirstName.error = null
        }

        if (!patronymic.isBlank()) {
            if (!namesRegex1.matches(patronymic)) {
                tilPatronymic.error = "Отчество должно состоять только из букв"
                isValid = false
            }
            else if (!namesRegex2.matches(patronymic)) {
                tilPatronymic.error = "Первая буква - строчная, остальные - прописные"
                isValid = false
            }
            else if (!namesRegex3.matches(patronymic)) {
                tilPatronymic.error = "Минимальная длина 2, максимальная - 50"
                isValid = false
            }
            else {
                tilPatronymic.error = null
            }
        }
        else {
            tilPatronymic.error = null
        }

        if (birth.isBlank()) {
            tilBirth.error = "Введите дату рождения"
            isValid = false
        }
        else {
            tilBirth.error = null
        }

        val driveLicenseRegex1 = Regex("""^\d*$""")
        val driveLicenseRegex2 = Regex("""^\d{10}$""")
        if (birth.isBlank()) {
            tilDriveLicense.error = "Введите дату рождения"
            isValid = false
        }
        else if (!driveLicenseRegex1.matches(driveLicense)) {
            tilDriveLicense.error = "Номер ВУ должен состоять только из цифр"
            isValid = false
        }
        else if (!driveLicenseRegex2.matches(driveLicense)) {
            tilDriveLicense.error = "Номер ВУ должен состоять только из 10 цифр"
            isValid = false
        }
        else {
            tilDriveLicense.error = null
        }

        if (password.isBlank()) {
            tilPassword.error = "Введите пароль"
            isValid = false
        }
        else if (password.length < 8) {
            tilPassword.error = "Минимальная длина пароля - 8 символов"
            isValid = false
        }
        else if (password.length > 32) {
            tilPassword.error = "Максимальная длина пароля - 32 символа"
            isValid = false
        }
        else {
            tilPassword.error = null
        }

        if (tilPassword.error == null)
        {
            if (repeatPassword.isBlank()) {
                tilRepeatPassword.error = "Повторите пароль"
                isValid = false
            }
            else if (password != repeatPassword) {
                tilRepeatPassword.error = "Пароли не совпадают"
                isValid = false
            }
            else {
                tilRepeatPassword.error = null
            }
        }
        else {
            tilRepeatPassword.error = null
        }

    }

    private fun signIn() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}