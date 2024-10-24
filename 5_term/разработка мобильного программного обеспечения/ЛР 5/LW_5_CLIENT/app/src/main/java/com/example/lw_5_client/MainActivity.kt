package com.example.lw_5_client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.displayCompositions).setOnClickListener {
            val fromMinutes = findViewById<EditText>(R.id.fromMinutes).text.toString()
            val fromSeconds = findViewById<EditText>(R.id.fromSeconds).text.toString()
            val toMinutes = findViewById<EditText>(R.id.toMinutes).text.toString()
            val toSeconds = findViewById<EditText>(R.id.toSeconds).text.toString()
            if (
                this.validMinutesFormat(fromMinutes) &&
                this.validSecondsFormat(fromSeconds) &&
                this.validMinutesFormat(toMinutes) &&
                this.validSecondsFormat(toSeconds) &&
                this.validTiming(fromMinutes, fromSeconds, toMinutes, toSeconds)
            ) {
                val intent = Intent(this, FilteredCompositionsActivity::class.java)
                intent.putExtra("fromMinutes", fromMinutes.toInt())
                intent.putExtra("fromSeconds", fromSeconds.toInt())
                intent.putExtra("toMinutes", toMinutes.toInt())
                intent.putExtra("toSeconds", toSeconds.toInt())
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Некорректный формат диапазона", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validTiming(fromMinutes: String, fromSeconds: String, toMinutes: String, toSeconds: String): Boolean {
        if (fromMinutes.toInt() < toMinutes.toInt()) {
            return true
        }
        else if (fromMinutes.toInt() == toMinutes.toInt()) {
            if (fromSeconds.toInt() < toSeconds.toInt()) {
                return true
            }
            return false
        }
        else {
            return false
        }
    }

    private fun validSecondsFormat(seconds: String): Boolean {
        if (seconds == "") {
            return false
        }
        if (seconds.toInt() > 60) {
            return false
        }
        if (seconds.toInt() < 0) {
            return false
        }
        return true
    }

    private fun validMinutesFormat(minutes: String): Boolean {
        if (minutes == "") {
            return false
        }
        if (minutes.toInt() < 0) {
            return false
        }
        return true
    }
}