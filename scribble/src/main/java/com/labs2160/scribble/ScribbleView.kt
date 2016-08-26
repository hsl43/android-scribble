package com.labs2160.scribble

import android.content.Context
import android.graphics.*
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class ScribbleView : RelativeLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        addView(bezelView)
        addView(inkStartAreaView)
    }

    private val bezelView = View(context).apply {
        id = R.id.scribble_view_bezel

        val widthRestriction  = ViewGroup.LayoutParams.WRAP_CONTENT
        val heightRestriction = ViewGroup.LayoutParams.MATCH_PARENT

        layoutParams = RelativeLayout.LayoutParams(widthRestriction, heightRestriction).apply { width = 100 }

        setBackgroundColor(Color.BLACK)

        setOnTouchListener { view, motionEvent ->
            var result = false

            if(view.id == R.id.scribble_view_bezel) {
                result = true

                inkStartAreaView.y = motionEvent.y - (inkStartAreaView.height / 2)

                if(inkStartAreaView.visibility == View.GONE) {
                    inkStartAreaView.visibility = View.VISIBLE
                }
            }

            result
        }
    }

    private val inkStartAreaView = View(context).apply {
        id = R.id.scribble_view_ink_start_area

        val widthRestriction  = ViewGroup.LayoutParams.MATCH_PARENT
        val heightRestriction = ViewGroup.LayoutParams.WRAP_CONTENT

        layoutParams = RelativeLayout.LayoutParams(widthRestriction, heightRestriction).apply { height = 100 }

        setBackgroundColor(Color.GRAY)

        visibility = View.GONE
    }

    private val path = Path()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither    = true
        color       = Color.BLACK
        style       = Paint.Style.STROKE
        strokeJoin  = Paint.Join.ROUND
        strokeCap   = Paint.Cap.ROUND
        strokeWidth = 6F
    }

    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvas: Canvas

    private var lastX = 0F
    private var lastY = 0F

//    private var showingInkStart

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

        val currentX = event.x
        val currentY = event.y

        when(MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_DOWN -> {
                path.reset()
                path.moveTo(currentX, currentY)

                invalidate()

                lastX = currentX
                lastY = currentY
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(currentX - lastX)
                val dy = Math.abs(currentY - lastY)

                if(dx >= 4 || dy >= 4) {
                    path.quadTo(lastX, lastY, (currentX + lastX) / 2, (currentY + lastY) / 2)

                    invalidate()

                    lastX = currentX
                    lastY = currentY
                }
            }

            MotionEvent.ACTION_UP -> {
                path.lineTo(lastX, lastY)

                canvas.drawPath(path, paint)

                path.reset()

                invalidate()
            }

            MotionEvent.ACTION_CANCEL  -> { /* unimplemented */ }
            MotionEvent.ACTION_OUTSIDE -> { /* unimplemented */ }
        }

        return true
    }
}