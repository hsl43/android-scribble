package com.labs2160.scribble

import android.content.Context
import android.graphics.*
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
        private const val bezelBackgroundColor = Color.LTGRAY

        private const val inkStartAreaHeight = 63
        private const val inkStartAreaBackgroundColor = Color.GRAY
        private const val inkStartAreaAlpha = .25F
        private const val inkStartAreaDisplayDuration = 3000L
        private const val inkStartAreaCenteringBackoffPeriod = 500L

        private const val scribbleTolerance = 4
        private const val scribbleColor = Color.BLACK
        private const val scribbleStrokeWidth = 6F
    }

    private var bezelViewPrimaryPointerId = -1
    private var bezelViewSecondaryPointerId = -1

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

                if(motionEvent.compatPointerCount() > 2) {
                    Log.i(javaClass.name, "No more than two concurrent touch points are supported...")
                } else {
                    when(motionEvent.compatAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            removeCallbacks(hideInkStartArea)

                            bezelViewPrimaryPointerId = motionEvent.compatPointerId()

                            inkStartAreaView.centerY(motionEvent)

                            if(inkStartAreaView.visibility == View.INVISIBLE) {
                                inkStartAreaView.visibility = View.VISIBLE
                            }
                        }

                        MotionEvent.ACTION_POINTER_DOWN -> {
                            bezelViewSecondaryPointerId = motionEvent.compatPointerId()
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val twoPointTouch = if(bezelViewSecondaryPointerId > -1) true else false

                            if(twoPointTouch) {
                                val firstTouchY  = motionEvent.compatY(bezelViewPrimaryPointerId)
                                val secondTouchY = motionEvent.compatY(bezelViewSecondaryPointerId)

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
                                if(System.currentTimeMillis() >= inkStartAreaResumeCenteringAsOf) {
                                    inkStartAreaView.centerY(motionEvent)
                                }
                            }
                        }

                        MotionEvent.ACTION_POINTER_UP -> {
                            bezelViewSecondaryPointerId = -1

                            inkStartAreaResumeCenteringAsOf = System.currentTimeMillis() + inkStartAreaCenteringBackoffPeriod
                        }

                        MotionEvent.ACTION_UP -> {
                            bezelViewPrimaryPointerId = -1

                            inkStartAreaResumeCenteringAsOf = System.currentTimeMillis()

                            postDelayed(hideInkStartArea, inkStartAreaDisplayDuration)
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            bezelViewPrimaryPointerId = -1
                            bezelViewSecondaryPointerId = -1
                        }

                        MotionEvent.ACTION_OUTSIDE -> { /* unimplemented */ }
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

        visibility = View.INVISIBLE

        setBackgroundColor(inkStartAreaBackgroundColor)
    }

    private val inkStartAreaTransaction = { behavior: () -> Unit ->
        removeCallbacks(hideInkStartArea)

        behavior()

        postDelayed(hideInkStartArea, inkStartAreaDisplayDuration)
    }

    private var inkStartAreaResumeCenteringAsOf = System.currentTimeMillis()

    private val hideInkStartArea = Runnable { inkStartAreaView.visibility = View.INVISIBLE }

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

    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvas: Canvas

    private var lastScribbleX = 0F
    private var lastScribbleY = 0F

//    private var bezelRect = RectF(0F, 0F, 0F, 0F)
//    private var inkStartAreaRect = RectF(0F, 0F, 0F, 0F)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0F, 0F, scribblePaint)
        canvas.drawPath(scribblePath, scribblePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(inkStartAreaView.visibility == View.INVISIBLE) {
            return false
        }

        if(event.intersects(inkStartAreaView)) {
            Log.d(javaClass.name, "## writing within inkStartAreaView...")

            when(event.compatAction()) {
                MotionEvent.ACTION_DOWN -> inkStartAreaTransaction {
                        val x = event.compatX(0)
                        val y = event.compatY(0)

                        scribblePath.reset()
                        scribblePath.moveTo(x, y)

                        invalidate()

                        lastScribbleX = x
                        lastScribbleY = y
                    }

                MotionEvent.ACTION_MOVE -> {
                    val x = event.compatX(0)
                    val y = event.compatY(0)

                    val dx = Math.abs(x - lastScribbleX)
                    val dy = Math.abs(y - lastScribbleY)

                    if(dx >= scribbleTolerance || dy >= scribbleTolerance) {
                        inkStartAreaTransaction {
                            scribblePath.quadTo(lastScribbleX, lastScribbleY, (x + lastScribbleX) / 2, (y + lastScribbleY) / 2)

                            invalidate()

                            lastScribbleX = x
                            lastScribbleY = y
                        }
                    }
                }

                MotionEvent.ACTION_UP -> inkStartAreaTransaction {
                    scribblePath.lineTo(lastScribbleX, lastScribbleY)

                    canvas.drawPath(scribblePath, scribblePaint)

                    scribblePath.reset()

                    invalidate()
                }

                MotionEvent.ACTION_CANCEL -> inkStartAreaTransaction { }

                MotionEvent.ACTION_OUTSIDE -> { /* unimplemented */ }
            }

            return true

        } else {
            return false
        }
    }
}