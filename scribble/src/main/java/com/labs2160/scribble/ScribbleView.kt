package com.labs2160.scribble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

class ScribbleView(context: Context): View(context) {
    private val strokePath = Path()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither    = true
        color       = Color.BLACK
        style       = Paint.Style.STROKE
        strokeJoin  = Paint.Join.ROUND
        strokeCap   = Paint.Cap.ROUND
        strokeWidth = 3F
    }

    private var lastX = 0F
    private var lastY = 0F

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(strokePath, paint)

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null) {
            return false
        }

        val currentX = event.x
        val currentY = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                strokePath.moveTo(currentX, currentY)

                lastX = currentX
                lastY = currentY
            }

            MotionEvent.ACTION_MOVE -> {
                strokePath.quadTo(lastX, lastY, (currentX + lastX) / 2, (currentY + lastY) /2)

                lastX = currentX
                lastY = currentY
            }

            MotionEvent.ACTION_UP -> {
//                path.lineTo(lastX, lastY)
            }
        }

        return true
    }
}