package com.example.lw_3_1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {
    private lateinit var selectedFigure: String
    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.selectedFigure = intent.getStringExtra("selectedFigure").toString()
        this.r = intent.getIntExtra("r", 0)
        this.g = intent.getIntExtra("g", 0)
        this.b = intent.getIntExtra("b", 0)
        val positions: ArrayList<Int>? = intent.getIntegerArrayListExtra("positions")

        if (this.selectedFigure == "Rectangle") {
            var rectangles = RectangleView(this)
            rectangles.init(this.r, this.g, this.b, positions)
            setContentView(rectangles)
        }
        else {
            var circles = CircleView(this)
            circles.init(this.r, this.g, this.b, positions)
            setContentView(circles)
        }
    }
}