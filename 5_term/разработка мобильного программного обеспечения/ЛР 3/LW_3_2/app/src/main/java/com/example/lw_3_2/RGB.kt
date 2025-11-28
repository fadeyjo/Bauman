package com.example.lw_3_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RGB : AppCompatActivity() {
    private var positions: ArrayList<Int>? = ArrayList()
    private var r: Int = -1
    private var g: Int = -1
    private var b: Int = -1
    private lateinit var button: Button
    private lateinit var editText: EditText

    private lateinit var selectedFigure: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rgb)

        this.positions = intent.getIntegerArrayListExtra("positions")
        this.selectedFigure = intent.getStringExtra("selectedFigure").toString()

        this.button = findViewById(R.id.goButton)
        this.button.setOnClickListener {
            this.editText = findViewById(R.id.editText)
            this.r = this.editText.text.toString().split(" ")[0].toInt()
            this.g = this.editText.text.toString().split(" ")[1].toInt()
            this.b = this.editText.text.toString().split(" ")[2].toInt()
            val intent = Intent(this, MainActivity::class.java)
            intent.putIntegerArrayListExtra("positions", this.positions)
            intent.putExtra("selectedFigure", this.selectedFigure)
            intent.putExtra("r", this.r)
            intent.putExtra("g", this.g)
            intent.putExtra("b", this.b)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}