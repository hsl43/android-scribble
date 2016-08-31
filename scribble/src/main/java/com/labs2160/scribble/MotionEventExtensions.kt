package com.labs2160.scribble

import android.graphics.Rect
import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent
import android.view.View

fun MotionEvent.compatAction() = MotionEventCompat.getActionMasked(this)

fun MotionEvent.compatPointerId() = MotionEventCompat.getPointerId(this, this.actionIndex)

fun MotionEvent.compatPointerCount() = MotionEventCompat.getPointerCount(this)

fun MotionEvent.compatX(pointerId: Int) = MotionEventCompat.getX(this, MotionEventCompat.findPointerIndex(this, pointerId))

fun MotionEvent.compatY(pointerId: Int) = MotionEventCompat.getY(this, MotionEventCompat.findPointerIndex(this, pointerId))

fun MotionEvent.intersects(view: View): Boolean {
    val coords = intArrayOf(0, 0)

    view.getLocationInWindow(coords)

    val rect = Rect(
            coords[0],
            coords[1],
            coords[0] + view.width,
            coords[1] + view.height)

    return rect.contains(compatX(0).toInt(), compatY(0).toInt())
}