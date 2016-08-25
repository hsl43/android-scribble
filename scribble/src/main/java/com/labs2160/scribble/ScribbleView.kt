package com.labs2160.scribble

import android.content.Context
import android.graphics.*
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ScribbleView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    companion object {
        private val TOUCH_TOLERANCE = 4
        private val STROKE_WIDTH = 6F
    }

    private val path = Path()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither    = true
        color       = Color.BLACK
        style       = Paint.Style.STROKE
        strokeJoin  = Paint.Join.ROUND
        strokeCap   = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }

    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvas: Canvas

    private var lastX = 0F
    private var lastY = 0F

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas       = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            drawBitmap(canvasBitmap, 0F, 0F, paint)
            drawPath(path, paint)
        }
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
                path.reset()
                path.moveTo(currentX, currentY)

                lastX = currentX
                lastY = currentY
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(currentX - lastX)
                val dy = Math.abs(currentY - lastY)

                if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(lastX, lastY, (currentX + lastX) / 2, (currentY + lastY) / 2)

                    lastX = currentX
                    lastY = currentY
                }
            }

            MotionEvent.ACTION_UP -> {
                path.lineTo(lastX, lastY)

                canvas.drawPath(path, paint)

                path.reset()
            }

            MotionEvent.ACTION_CANCEL  -> { /* unimplemented */ }
            MotionEvent.ACTION_OUTSIDE -> { /* unimplemented */ }
        }

        invalidate()

        return true
    }

    fun clear() {
        if(!path.isEmpty) {
            path.reset()
            invalidate()
        }
    }
}