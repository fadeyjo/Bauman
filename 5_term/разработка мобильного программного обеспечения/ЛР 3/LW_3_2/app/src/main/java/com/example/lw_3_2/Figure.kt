package com.example.lw_3_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Figure : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var button: Button
    private lateinit var selectedFigure: String
    private lateinit var rectangleRadio: RadioButton
    private lateinit var circleRadio: RadioButton
    private var positions: ArrayList<Int>? = ArrayList()
    private var r: Int = -1
    private var g: Int = -1
    private var b: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_figure)

        this.positions = intent.getIntegerArrayListExtra("positions")
        this.r = intent.getIntExtra("r", -1)
        this.g = intent.getIntExtra("g", -1)
        this.b = intent.getIntExtra("b", -1)

        this.circleRadio = findViewById(R.id.circleRadioButton)
        this.rectangleRadio = findViewById(R.id.rectangleRadioButton)

        this.radioGroup = findViewById(R.id.radioGroup)
        this.radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener{ group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                val text: String = radio.text.toString()
                this.selectedFigure = text
            }

        )

        this.button = findViewById(R.id.selectionButton)
        this.button.setOnClickListener {
            if (!this.rectangleRadio.isChecked && !this.circleRadio.isChecked) {
                Toast.makeText(this, "Select a figure!!!", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putIntegerArrayListExtra("positions", this.positions)
                intent.putExtra("selectedFigure", this.selectedFigure)
                intent.putExtra("r", this.r)
                intent.putExtra("g", this.g)
                intent.putExtra("b", this.b)
                startActivity(intent)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}