package com.example.lw_3_1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class CircleView(context: Context?): View(context) {
    private val paint: Paint = Paint()

    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0
    private var positions: ArrayList<Int>? = ArrayList()
    private  var radius: Float = 100f

    fun init(r: Int, g: Int, b: Int, positions: ArrayList<Int>?) {
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

        this.positions?.let { postList ->
            val metrics = context.resources.displayMetrics
            val horizontalPadding: Float = (metrics.widthPixels - this.radius * 6) / 4
            val verticalPadding: Float = (metrics.heightPixels - this.radius * 6) / 4

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