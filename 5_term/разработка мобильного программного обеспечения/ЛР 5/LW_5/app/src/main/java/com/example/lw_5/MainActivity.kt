package com.example.lw_5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fromMinutes = findViewById<EditText>(R.id.fromMinutes)
        val fromSeconds = findViewById<EditText>(R.id.fromSeconds)
        val toMinutes = findViewById<EditText>(R.id.toMinutes)
        val toSeconds = findViewById<EditText>(R.id.toSeconds)
        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            try {
                val minSecondsValue =
                    fromSeconds.text.toString().toIntOrNull() ?: 0
                val maxSecondsValue =
                    toSeconds.text.toString().toIntOrNull() ?: 0
                if (minSecondsValue >= 60 || maxSecondsValue >= 60) {
                    Toast.makeText(this, "Seconds must be smaller",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val minDuration =
                    (fromMinutes.text.toString().toIntOrNull() ?: 0) * 60 +
                            minSecondsValue
                val maxDuration =
                    (toMinutes.text.toString().toIntOrNull() ?: 0) * 60 +
                            maxSecondsValue
                if (maxDuration < minDuration) {
                    Toast.makeText(this, "Max duration must be greater", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                }
                val intent = Intent(this, TracksActivity::class.java).apply {
                    putExtra("from", minDuration * 1000L)
                    putExtra("to", maxDuration * 1000L)
                }
                startActivity(intent)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Enter valid numbers",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}