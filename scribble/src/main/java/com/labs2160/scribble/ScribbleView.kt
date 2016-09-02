package com.labs2160.scribble

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.RelativeLayout

class ScribbleView : RelativeLayout, BezelView.BezelInteractionListener, InkStartAreaView.InkStartAreaInteractionListener {
    private val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            cancelHidingInkStartArea()

            inkStartAreaScaleFactor *= detector.scaleFactor

            inkStartAreaView.scaleY(inkStartAreaScaleFactor)

            scheduleHidingInkStartArea()

            return true
        }
    })

    private val hideInkStartArea = Runnable { inkStartAreaView.hide() }

    private val cancelHidingInkStartArea = { removeCallbacks(hideInkStartArea) }

    private val scheduleHidingInkStartArea = { postDelayed(hideInkStartArea, inkStartAreaDisplayDuration) }

    private val scribblePath = Path()

    private val scribblePaint = Paint().apply {
        isAntiAlias = true
        isDither    = true
        color       = scribbleColor
        style       = Paint.Style.STROKE
        strokeJoin  = Paint.Join.ROUND
        strokeCap   = Paint.Cap.ROUND
        strokeWidth = scribbleStrokeWidth
    }

    private lateinit var bezelView: BezelView
    private lateinit var inkStartAreaView: InkStartAreaView

    private lateinit var scribbleCanvasBitmap: Bitmap
    private lateinit var scribbleCanvas: Canvas

    private var scribbleLastX = 0F
    private var scribbleLastY = 0F

    private var inkStartAreaScaleFactor = 2.5F

    companion object {
        private const val scribbleTolerance = 4
        private const val scribbleColor = Color.BLACK
        private const val scribbleStrokeWidth = 6F

        private const val inkStartAreaDisplayDuration = 1000L
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        bezelView = BezelView(context, attrs).apply { setListener(this@ScribbleView) }
        inkStartAreaView = InkStartAreaView(context, attrs).apply { setListener(this@ScribbleView) }

        addView(bezelView)
        addView(inkStartAreaView)

        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        scribbleCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        scribbleCanvas = Canvas(scribbleCanvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(scribbleCanvasBitmap, 0F, 0F, scribblePaint)
        canvas.drawPath(scribblePath, scribblePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(inkStartAreaView.visibility != View.VISIBLE) {
            return false
        } else {
            scaleGestureDetector.onTouchEvent(event)

            return true
        }
    }

    override fun onBezelTouch(x: Float, y: Float) {
        cancelHidingInkStartArea()

        inkStartAreaView.positionY(y, true)
        inkStartAreaView.show()
    }

    override fun onBezelLongPress() {
        Log.d(javaClass.name, "## beginning onBezelLongPress()...")
    }

    override fun onBezelMove(x: Float, y: Float) {
        inkStartAreaView.positionY(y, true)
    }

    override fun onBezelRelease(x: Float, y: Float) {
        scheduleHidingInkStartArea()
    }

    override fun onInkStartAreaTouch(x: Float, y: Float) {
        cancelHidingInkStartArea()

        scribblePath.reset()
        scribblePath.moveTo(x, y)

        invalidate()

        scribbleLastX = x
        scribbleLastY = y
    }

    override fun onInkStartAreaMove(x: Float, y: Float) {
        val dx = Math.abs(x - scribbleLastX)
        val dy = Math.abs(y - scribbleLastY)

        if(dx >= scribbleTolerance || dy >= scribbleTolerance) {
            scribblePath.quadTo(scribbleLastX, scribbleLastY, (x + scribbleLastX) / 2, (y + scribbleLastY) / 2)

            invalidate()

            scribbleLastX = x
            scribbleLastY = y
        }
    }

    override fun onInkStartAreaRelease(x: Float, y: Float) {
        scribblePath.lineTo(scribbleLastX, scribbleLastY)

        scribbleCanvas.drawPath(scribblePath, scribblePaint)

        scribblePath.reset()

        invalidate()

        scheduleHidingInkStartArea()
    }
}