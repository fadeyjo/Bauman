package com.example.lw_3_1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint: Paint = Paint()

    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0
    private var width: Float = 200F
    private var height: Float = 100F
    private  var radius: Float = 50f
    private var positions: ArrayList<Int>? = ArrayList()
    private var selectedFigure: String = "Rectangle";

    private lateinit var can: Canvas

    fun init(r: Int, g: Int, b: Int, positions: ArrayList<Int>?, selectedFigure: String) {
        this.selectedFigure = selectedFigure
        this.positions = positions
        this.r = r
        this.g = g
        this.b = b
        this.paint.apply {
            style = Paint.Style.FILL
            color = Color.rgb(r, g, b)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.can = canvas

        if (this.selectedFigure == "Rectangle") {
            this.positions?.let { postList ->
                val metrics = context.resources.displayMetrics
                val horizontalPadding: Float = (metrics.widthPixels - this.width * 3) / 4
//                val verticalPadding: Float = (metrics.heightPixels - this.height * 3) / 4
                val verticalPadding: Float = (400 - this.height * 3) / 4
                var leftPadding = 0F
                var topPadding = 0F
                var rightPadding = 0F
                var bottomPadding = 0F
                var row = 0
                var col = 0

                for (post in postList) {
                    row = post / 3
                    col = post % 3
                    leftPadding =
                        (col + 1).toFloat() * horizontalPadding + this.width * col.toFloat()
                    topPadding = (row + 1).toFloat() * verticalPadding + this.height * row.toFloat()
                    rightPadding =
                        (col + 1).toFloat() * horizontalPadding + this.width * (col + 1).toFloat()
                    bottomPadding =
                        (row + 1).toFloat() * verticalPadding + this.height * (row + 1).toFloat()
                    canvas.drawRect(
                        leftPadding,
                        topPadding,
                        rightPadding,
                        bottomPadding,
                        this.paint
                    )
                }
            }
        }
        else if (this.selectedFigure == "Circle"){
            this.positions?.let { postList ->
                val metrics = context.resources.displayMetrics
                val horizontalPadding: Float = (metrics.widthPixels - this.radius * 6) / 4
//                val verticalPadding: Float = (metrics.heightPixels - this.radius * 6) / 4
                val verticalPadding: Float = (400 - this.height * 3) / 4
                var cx = 0F
                var cy = 0F
                var row = 0
                var col = 0

                for (post in postList) {
                    row = post / 3
                    col = post % 3
                    cx = (1 + col).toFloat() * horizontalPadding + this.radius * ((2 * col).toFloat() + 1)
                    cy = (1 + row).toFloat() * verticalPadding + this.radius * ((2 * row).toFloat() + 1)
                    canvas.drawCircle(cx, cy, this.radius, this.paint)
                }
            }
        }
    }
}