package com.example.lw_3_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var drawButton: Button
    private var positions: MutableList<Int> = ArrayList()
    private lateinit var selectedFigure: String
    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        this.selectedFigure = intent.getStringExtra("selectedFigure").toString()
        this.r = intent.getIntExtra("r", 0)
        this.g = intent.getIntExtra("g", 0)
        this.b = intent.getIntExtra("b", 0)

        this.button1 = findViewById(R.id.button1)
        this.button2 = findViewById(R.id.button2)
        this.button3 = findViewById(R.id.button3)
        this.button4 = findViewById(R.id.button4)
        this.button5 = findViewById(R.id.button5)
        this.button6 = findViewById(R.id.button6)
        this.button7 = findViewById(R.id.button7)
        this.button8 = findViewById(R.id.button8)
        this.button9 = findViewById(R.id.button9)

        this.drawButton = findViewById(R.id.drawButton)
        this.drawButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_CALL
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("selectedFigure", this.selectedFigure)
            intent.putExtra("r", this.r)
            intent.putExtra("g", this.g)
            intent.putExtra("b", this.b)
            intent.putIntegerArrayListExtra("positions", ArrayList(this.positions))
            startActivity(intent)
            button1.isEnabled = true
            button2.isEnabled = true
            button3.isEnabled = true
            button4.isEnabled = true
            button5.isEnabled = true
            button6.isEnabled = true
            button7.isEnabled = true
            button8.isEnabled = true
            button9.isEnabled = true
            this.positions = ArrayList()
        }

        this.button1.setOnClickListener {
            button1.isEnabled = false
            this.positions.add(0)
        }

        this.button2.setOnClickListener {
            button2.isEnabled = false
            this.positions.add(1)
        }

        this.button3.setOnClickListener {
            button3.isEnabled = false
            this.positions.add(2)
        }

        this.button4.setOnClickListener {
            button4.isEnabled = false
            this.positions.add(3)
        }

        this.button5.setOnClickListener {
            button5.isEnabled = false
            this.positions.add(4)
        }

        this.button6.setOnClickListener {
            button6.isEnabled = false
            this.positions.add(5)
        }

        this.button7.setOnClickListener {
            button7.isEnabled = false
            this.positions.add(6)
        }

        this.button8.setOnClickListener {
            button8.isEnabled = false
            this.positions.add(7)
        }

        this.button9.setOnClickListener {
            button9.isEnabled = false
            this.positions.add(8)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}