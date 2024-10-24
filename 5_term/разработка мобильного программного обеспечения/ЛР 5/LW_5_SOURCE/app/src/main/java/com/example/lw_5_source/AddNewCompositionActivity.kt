package com.example.lw_5_source

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddNewCompositionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_composition)

        findViewById<Button>(R.id.mainButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            val author = findViewById<EditText>(R.id.authorEditText).text.toString()
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val duration = findViewById<EditText>(R.id.durationEditText).text.toString()

            if (author != "" && title != "" && duration != "") {
                CompositionDBHelper(this, null).addComposition(Composition(author, title, duration.toInt()))
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Некорректный ввод", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}