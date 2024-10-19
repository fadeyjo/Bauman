package com.example.lw_3_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lw_3_1.CustomView

class MainActivity : AppCompatActivity() {
    private lateinit var selectedFigure: String
    private var r: Int = -1
    private var g: Int = -1
    private var b: Int = -1
    private lateinit var figureButton: Button
    private lateinit var positionsButton: Button
    private lateinit var rgbButton: Button
    private var positions: ArrayList<Int>? = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.selectedFigure = intent.getStringExtra("selectedFigure").toString()
        this.r = intent.getIntExtra("r", -1)
        this.g = intent.getIntExtra("g", -1)
        this.b = intent.getIntExtra("b", -1)
        this.positions = intent.getIntegerArrayListExtra("positions")

        if (this.r >= 0 && this.g >= 0 && this.b >= 0  && this.positions.toString() != "null") {
            Toast.makeText(this, this.selectedFigure, Toast.LENGTH_SHORT).show()
            setContentView(R.layout.activity_main)
            val req: LinearLayout = findViewById(R.id.req)
            val custom = CustomView(this)
            custom.init(this.r, this.g, this.b, this.positions, this.selectedFigure)
            req.addView(custom)
        }
        else {
            setContentView(R.layout.activity_main)

            this.figureButton = findViewById(R.id.figureButton)
            this.positionsButton = findViewById(R.id.positionsButton)
            this.rgbButton = findViewById(R.id.RGBButton)

            this.positionsButton.setOnClickListener {
                val intent = Intent(this, Positions::class.java)
                intent.putExtra("selectedFigure", this.selectedFigure)
                intent.putExtra("r", this.r)
                intent.putExtra("g", this.g)
                intent.putExtra("b", this.b)
                startActivity(intent)
            }

            this.figureButton.setOnClickListener {
                val intent = Intent(this, Figure::class.java)
                intent.putIntegerArrayListExtra("positions", this.positions)
                intent.putExtra("r", this.r)
                intent.putExtra("g", this.g)
                intent.putExtra("b", this.b)
                startActivity(intent)
            }

            this.rgbButton.setOnClickListener {
                val intent = Intent(this, RGB::class.java)
                intent.putIntegerArrayListExtra("positions", this.positions)
                intent.putExtra("selectedFigure", this.selectedFigure)
                startActivity(intent)
            }
        }
    }
}