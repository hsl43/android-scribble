package com.labs2160.scribble

import android.content.Context
import android.graphics.Color
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class BezelView : View {
    private val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            listener?.onBezelLongPress()
        }
    })

    private var activePointerId = -1

    private var listener: BezelInteractionListener? = null

    companion object {
        private const val bezelWidth = 40
        private const val bezelBackgroundColor = Color.LTGRAY
    }

    interface BezelInteractionListener {
        fun onBezelTouch(x: Float, y: Float)
        fun onBezelLongPress()
        fun onBezelMove(x: Float, y: Float)
        fun onBezelRelease(x: Float, y: Float)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        id = R.id.scribble_view_bezel

        val widthRestriction  = ViewGroup.LayoutParams.WRAP_CONTENT
        val heightRestriction = ViewGroup.LayoutParams.MATCH_PARENT

        layoutParams = RelativeLayout.LayoutParams(widthRestriction, heightRestriction).apply { width = bezelWidth }

        setBackgroundColor(bezelBackgroundColor)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerCount = event.compatPointerCount()

        if(pointerCount > 2) {
            Log.i(javaClass.name, "No more than two concurrent touch points are supported...")

            return false

        } else {
            gestureDetector.onTouchEvent(event)

            when(event.compatAction()) {
                MotionEvent.ACTION_DOWN -> {
                    activePointerId = event.compatPointerId(0)

                    listener?.onBezelTouch(event.compatX(activePointerId), event.compatY(activePointerId))
                }

                MotionEvent.ACTION_MOVE -> {
                    listener?.onBezelMove(event.compatX(activePointerId), event.compatY(activePointerId))
                }

                MotionEvent.ACTION_UP -> {
                    listener?.onBezelRelease(event.compatX(activePointerId), event.compatY(activePointerId))

                    activePointerId = -1
                }

                MotionEvent.ACTION_POINTER_UP -> {
                    val actionIndex = event.compatActionIndex()

                    if(event.compatPointerId(actionIndex) == activePointerId) {
                        activePointerId = event.compatPointerId(if(actionIndex == 0) 1 else 0)
                    }
                }
            }
        }

        return true
    }

    fun setListener(listener: BezelInteractionListener) {
        this.listener = listener
    }
}