package com.example.lw_4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NewLaptop : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_laptop)

        val newLaptopButton = findViewById<Button>(R.id.newLaptopButton)
        newLaptopButton.setOnClickListener {
            val manufacturerName = findViewById<EditText>(R.id.manufacturerName).text.toString()
            val HDDVolume = findViewById<EditText>(R.id.HDDVolume).text.toString()
            val SSDPresent = findViewById<CheckBox>(R.id.SSDPresent).isChecked
            val RAMVolume = findViewById<EditText>(R.id.RAMVolume).text.toString()
            val isFHD = findViewById<CheckBox>(R.id.isFHD).isChecked
            val screenTime = findViewById<EditText>(R.id.screenTime).text.toString()

            if (manufacturerName == "") {
                Toast.makeText(this, "Введите производителя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Regex("^[0-9]+$").matches(HDDVolume)) {
                Toast.makeText(this, "Некорректный ввод объёма жесткого диска", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Regex("^[0-9]+$").matches(RAMVolume)) {
                Toast.makeText(this, "Некорректный ввод ОП", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Regex("^[0-9]+$").matches(screenTime)) {
                Toast.makeText(this, "Некорректный ввод времени автономной работы", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = DBHelper(this, null)
            db.addLaptop(manufacturerName, HDDVolume.toInt(), SSDPresent, RAMVolume.toInt(), isFHD, screenTime.toInt())

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}