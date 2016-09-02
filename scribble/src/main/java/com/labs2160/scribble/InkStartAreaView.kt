package com.labs2160.scribble

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class InkStartAreaView : View {
    private val locationInWindow = intArrayOf(0, 0)
    private val rectInWindow = Rect(0, 0, 0, 0)

    private var listener: InkStartAreaInteractionListener? = null

    private var lastX = 0F
    private var lastY = 0F

//    private var filterRule: FilterRule? = ChainFilterRule(listOf(
//            ContactSizeFilterRule(),
//            ContactPressureFilterRule(),
//            RelativePositionFilterRule()))

    companion object {
        private const val inkStartAreaHeight = 163
        private const val inkStartAreaBackgroundColor = Color.GRAY
        private const val inkStartAreaAlpha = .25F
    }

    interface InkStartAreaInteractionListener {
        fun onInkStartAreaTouch(x: Float, y: Float)
        fun onInkStartAreaMove(x: Float, y: Float)
        fun onInkStartAreaRelease(x: Float, y: Float)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        id = R.id.scribble_view_ink_start_area

        val widthRestriction  = ViewGroup.LayoutParams.MATCH_PARENT
        val heightRestriction = ViewGroup.LayoutParams.WRAP_CONTENT

        layoutParams = RelativeLayout.LayoutParams(widthRestriction, heightRestriction).apply { height = inkStartAreaHeight }

        alpha = inkStartAreaAlpha

        hide()

        setBackgroundColor(inkStartAreaBackgroundColor)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.compatAction()) {
            MotionEvent.ACTION_DOWN -> {
                val x = locationInWindow[0] + event.compatX(0)
                val y = locationInWindow[1] + event.compatY(0)

                listener?.onInkStartAreaTouch(x, y)

                lastX = x
                lastY = y

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val x = locationInWindow[0] + event.compatX(0)
                val y = locationInWindow[1] + event.compatY(0)

                if(rectInWindow.contains(x.toInt(), y.toInt())) {
                    listener?.onInkStartAreaMove(x, y)

                    lastX = x
                    lastY = y

                    return true

                } else {
                    return false
                }
            }

            MotionEvent.ACTION_UP -> {
                listener?.onInkStartAreaRelease(lastX, lastY)

                return true
            }

            else -> {
                return false
            }
        }
    }

    fun setListener(listener: InkStartAreaInteractionListener) {
        this.listener = listener
    }

    fun show() {
        if(visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
    }

    fun hide() {
        if(visibility != View.INVISIBLE) {
            visibility = View.INVISIBLE
        }
    }

    fun positionY(position: Float, center: Boolean = false) {
        this.y = if(center) position - (height / 2) else position

        exportTopLeftCornerInWindow(locationInWindow, rectInWindow)
    }

    fun scaleY(factor: Float) {
        scaleY = factor

        exportTopLeftCornerInWindow(locationInWindow, rectInWindow)
    }

    private fun exportTopLeftCornerInWindow(location: IntArray, rect: Rect) {
        getLocationInWindow(location)
        location[1] -= height

        rect.set(location[0], location[1], location[0] + width, location[1] + height)
    }
}