package com.labs2160.scribble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ScribbleView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val strokePaths = Path()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither    = true
        color       = Color.BLACK
        style       = Paint.Style.STROKE
        strokeJoin  = Paint.Join.ROUND
        strokeCap   = Paint.Cap.ROUND
        strokeWidth = 6F
    }

    private var lastX = 0F
    private var lastY = 0F

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(strokePaths, paint)

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null) {
            return false
        }

        if(event.size > .01F) {
            return false
        }

        val currentX = event.x
        val currentY = event.y

        when(MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_DOWN -> {
                strokePaths.moveTo(currentX, currentY)

                lastX = currentX
                lastY = currentY
            }

            MotionEvent.ACTION_MOVE -> {
                strokePaths.quadTo(lastX, lastY, (currentX + lastX) / 2, (currentY + lastY) / 2)

                lastX = currentX
                lastY = currentY
            }

            MotionEvent.ACTION_UP      -> { /* unimplemented */ }
            MotionEvent.ACTION_CANCEL  -> { /* unimplemented */ }
            MotionEvent.ACTION_OUTSIDE -> { /* unimplemented */ }
        }

        return true
    }

    fun clear() {
        if(!strokePaths.isEmpty) {
            strokePaths.reset()
            invalidate()
        }
    }
}