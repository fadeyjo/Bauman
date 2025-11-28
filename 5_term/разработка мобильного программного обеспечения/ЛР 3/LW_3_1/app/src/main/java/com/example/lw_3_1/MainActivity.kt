package com.example.lw_3_1

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

class MainActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var button: Button
    private lateinit var editText: EditText
    private lateinit var selectedFigure: String
    private lateinit var rgb: Array<Int>
    private lateinit var rectangleRadio: RadioButton
    private lateinit var circleRadio: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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
                this.editText = findViewById(R.id.editText)
                val pattern = Regex("^[0-9]{1,3} [0-9]{1,3} [0-9]{1,3}$")
                if (pattern.containsMatchIn(editText.text)) {
                    val text: String = editText.text.toString()
                    this.rgb = (text.split(" ").map { it.toInt() }).toTypedArray()
                    if (rgb[0] <= 255 && rgb[1] <= 255 && rgb[2] <= 255) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_ASSIST
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.putExtra("selectedFigure", this.selectedFigure)
                        intent.putExtra("r", this.rgb[0])
                        intent.putExtra("g", this.rgb[1])
                        intent.putExtra("b", this.rgb[2])
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "Select a figure!!!", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    Toast.makeText(this, "Incorrect format", Toast.LENGTH_LONG).show()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}