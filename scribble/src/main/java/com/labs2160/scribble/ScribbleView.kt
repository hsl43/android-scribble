package com.labs2160.scribble

import android.content.Context
import android.graphics.*
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class ScribbleView : RelativeLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        addView(bezelView)
        addView(inkStartAreaView)

        setWillNotDraw(false)
    }

    companion object {
        private const val bezelWidth = 25
        private const val bezelBackgroundColor = Color.BLACK

        private const val inkStartAreaHeight = 63
        private const val inkStartAreaBackgroundColor = Color.GRAY
        private const val inkStartAreaAlpha = .25F
        private const val inkStartAreaDisplayDuration = 3000L

        private const val scribbleTolerance = 4
        private const val scribbleColor = Color.BLACK
        private const val scribbleStrokeWidth = 6F
    }

    private val hideInkStartArea = Runnable { inkStartAreaView.visibility = View.GONE }

    private val bezelView = View(context).apply {
        id = R.id.scribble_view_bezel

        val widthRestriction  = ViewGroup.LayoutParams.WRAP_CONTENT
        val heightRestriction = ViewGroup.LayoutParams.MATCH_PARENT

        layoutParams = RelativeLayout.LayoutParams(widthRestriction, heightRestriction).apply { width = bezelWidth }

        setBackgroundColor(bezelBackgroundColor)

        setOnTouchListener { view, motionEvent ->
            var result = false

            if(view.id == R.id.scribble_view_bezel) {
                result = true

                val twoPointTouch = motionEvent.pointerCount == 2

                when(MotionEventCompat.getActionMasked(motionEvent)) {
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        Log.d(javaClass.name, "## multi-touch on bezel starting...")
                    }

                    MotionEvent.ACTION_POINTER_UP -> {
                        Log.d(javaClass.name, "## multi-touch on bezel ending...")
                    }

                    else -> {
                        removeCallbacks(hideInkStartArea)

                        if(twoPointTouch) {
                            val firstTouchY  = motionEvent.y
                            val secondTouchY = motionEvent.getY(1)

                            if(firstTouchY < secondTouchY) {
                                val dy = secondTouchY - firstTouchY

                                with(inkStartAreaView) {
                                    layoutParams = this.layoutParams.apply { height = inkStartAreaHeight + dy.toInt() }
                                }

                            } else if(firstTouchY > secondTouchY) {
                                val dy = firstTouchY - secondTouchY

                                with(inkStartAreaView) {
                                    layoutParams = this.layoutParams.apply { height = inkStartAreaHeight + dy.toInt() }

                                    y = secondTouchY
                                }
                            }

                        } else {
                            val newInkStartAreaY = motionEvent.y

                            inkStartAreaView.y = newInkStartAreaY

                            inkStartAreaRect.left   = 0
                            inkStartAreaRect.top    = newInkStartAreaY.toInt()
                            inkStartAreaRect.right  = inkStartAreaView.width
                            inkStartAreaRect.bottom = newInkStartAreaY.toInt() + inkStartAreaView.height

                            if(inkStartAreaView.visibility == View.GONE) {
                                inkStartAreaView.visibility = View.VISIBLE
                            }
                        }

                        postDelayed(hideInkStartArea, inkStartAreaDisplayDuration)
                    }
                }
            }

            result
        }
    }

    private val inkStartAreaView = View(context).apply {
        id = R.id.scribble_view_ink_start_area

        val widthRestriction  = ViewGroup.LayoutParams.MATCH_PARENT
        val heightRestriction = ViewGroup.LayoutParams.WRAP_CONTENT

        layoutParams = RelativeLayout.LayoutParams(widthRestriction, heightRestriction).apply { height = inkStartAreaHeight }

        alpha = inkStartAreaAlpha

        visibility = View.GONE

        setBackgroundColor(inkStartAreaBackgroundColor)
    }

    private val path = Path()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither    = true
        color       = scribbleColor
        style       = Paint.Style.STROKE
        strokeJoin  = Paint.Join.ROUND
        strokeCap   = Paint.Cap.ROUND
        strokeWidth = scribbleStrokeWidth
    }

    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvas: Canvas

    private var lastX = 0F
    private var lastY = 0F

    private var inkStartAreaRect = Rect(0, 0, 0, 0)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0F, 0F, paint)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(inkStartAreaView.visibility == View.GONE) {
            return false
        }

        val currentX = event.x
        val currentY = event.y

        if(inkStartAreaRect.contains(currentX.toInt(), currentY.toInt())) {
            when(MotionEventCompat.getActionMasked(event)) {
                MotionEvent.ACTION_DOWN -> {
                    removeCallbacks(hideInkStartArea)

                    path.reset()
                    path.moveTo(currentX, currentY)

                    invalidate()

                    lastX = currentX
                    lastY = currentY
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = Math.abs(currentX - lastX)
                    val dy = Math.abs(currentY - lastY)

                    if(dx >= scribbleTolerance || dy >= scribbleTolerance) {
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

                    postDelayed(hideInkStartArea, inkStartAreaDisplayDuration)
                }

                MotionEvent.ACTION_CANCEL  -> { /* unimplemented */ }
                MotionEvent.ACTION_OUTSIDE -> { /* unimplemented */ }
            }

            return true

        } else {
            return false
        }
    }
}